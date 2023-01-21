package com.code.korti.seaofimages.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.code.korti.seaofimages.data.Networking
import com.code.korti.seaofimages.data.db.Database
import com.code.korti.seaofimages.data.models.Collection
import com.code.korti.seaofimages.data.models.CollectionPhotos
import com.code.korti.seaofimages.data.models.CollectionWithPhotos
import com.code.korti.seaofimages.data.models.ImageFromList

class CollectionRepository() {

    private var collectionsList: List<Collection> = emptyList()
    private val collectionDao = Database.instance.collectionDao()

    suspend fun getCollectionsList(page: Int, numberOfCollections: Int): List<Collection> {
        val collections = Networking.api.getCollectionsList(page, numberOfCollections)
        collectionDao.insertCollectionsInDB(collections)
        collectionsList = collectionsList + collections
        return collectionsList
    }

    suspend fun getCollectionsFromDB(): List<Collection>{
        return collectionDao.getCollectionsFromDB()
    }

    suspend fun getCollectionWithPhotos(collectionId:String, page: Int, numberOfPhotos: Int): CollectionWithPhotos {
        val collection = Networking.api.getCollectionPhotos(collectionId, page, numberOfPhotos)
        val collectionPhotos = createCollectionPhotos(collection, collectionId)
        collectionDao.insertCollectionPhotosInDB(collectionPhotos)
        return collectionDao.getCollectionWithPhotosFromDB(collectionId)
    }
    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    suspend fun deleteAllCollectionsFromDB(){
        collectionDao.deleteAllCollectionsFromDB()
        collectionDao.deleteAllCollectionPhotosFromDB()
    }

    private fun createCollectionPhotos(images: List<ImageFromList>, collectionId: String): List<CollectionPhotos>{
        return images.map {
            CollectionPhotos(
                it.id,
                it.urls,
                it.width,
                it.height,
                it.likes,
                it.likedByUser,
                it.user,
                collectionId
            )
        }
    }
}