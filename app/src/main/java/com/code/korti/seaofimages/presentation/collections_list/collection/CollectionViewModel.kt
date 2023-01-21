package com.code.korti.seaofimages.presentation.collections_list.collection

import android.app.Application
import androidx.lifecycle.*
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.repository.CollectionRepository
import com.code.korti.seaofimages.data.models.CollectionWithPhotos
import com.code.korti.seaofimages.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class CollectionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CollectionRepository()
    private val context = application

    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val collectionWithPhotosLiveData = MutableLiveData<CollectionWithPhotos>()
    private val errorLiveEvent = SingleLiveEvent<Int>()

    val errorLiveData: LiveData<Int>
        get() = errorLiveEvent

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    val collectionWithPhotos: LiveData<CollectionWithPhotos>
        get() = collectionWithPhotosLiveData

    fun loadCollectionPhotos(collectionId: String, page: Int, numberOfPhotos: Int){
        viewModelScope.launch {
            try {
                if (repository.isConnected(context)) {
                isLoadingLiveData.postValue(true)
                val collectionWithPhotos = repository.getCollectionWithPhotos(collectionId, page, numberOfPhotos)
                collectionWithPhotosLiveData.postValue(collectionWithPhotos)
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (e: Throwable){
                Timber.d(e)
                errorLiveEvent.postValue(R.string.no_more_requests)
            } finally {
                isLoadingLiveData.postValue(false)
            }
        }
    }
}