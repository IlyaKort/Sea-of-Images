package com.code.korti.seaofimages.presentation.collections_list

import android.app.Application
import androidx.lifecycle.*
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.repository.CollectionRepository
import com.code.korti.seaofimages.data.models.Collection
import com.code.korti.seaofimages.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class CollectionsListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CollectionRepository()
    private val context = application

    private val collectionsListLiveData = MutableLiveData<List<Collection>>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val errorLiveEvent = SingleLiveEvent<Int>()

    val errorLiveData: LiveData<Int>
        get() = errorLiveEvent

    val collectionsList: LiveData<List<Collection>>
        get() = collectionsListLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    fun loadPage(page: Int, numberOfCollections: Int){
        viewModelScope.launch {
            try {
                if (repository.isConnected(context)) {
                isLoadingLiveData.postValue(true)
                val list = repository.getCollectionsList(page, numberOfCollections)
                collectionsListLiveData.postValue(list)
                } else {
                    errorLiveEvent.postValue(R.string.no_internet_connection)
                }
            } catch (t: Throwable){
                Timber.d(t)
                collectionsListLiveData.postValue(repository.getCollectionsFromDB())
                errorLiveEvent.postValue(R.string.no_more_requests)
            } finally {
                isLoadingLiveData.postValue(false)
            }
        }
    }
}