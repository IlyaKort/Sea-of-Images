package com.code.korti.seaofimages.presentation.search

import android.app.Application
import androidx.lifecycle.*
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.data.repository.ImageRepository
import com.code.korti.seaofimages.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ImageRepository(application)

    private val imageListLiveData = MutableLiveData<List<ImageFromList>>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val errorLiveEvent = SingleLiveEvent<Int>()

    val errorLiveData: LiveData<Int>
        get() = errorLiveEvent

    val imageList: LiveData<List<ImageFromList>>
        get() = imageListLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    fun search(query: String, page: Int, numberOfImages: Int) {
        viewModelScope.launch {
            try {
                if (repository.isConnected()) {
                    isLoadingLiveData.postValue(true)
                    imageListLiveData.postValue(repository.searchImage(query, page, numberOfImages))
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (e: Throwable) {
                Timber.d(e)
                errorLiveEvent.postValue(R.string.no_more_requests)
                imageListLiveData.postValue(emptyList())
            } finally {
                isLoadingLiveData.postValue(false)
            }
        }
    }
}