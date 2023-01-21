package com.code.korti.seaofimages.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.work.*
import com.code.korti.seaofimages.data.Networking
import com.code.korti.seaofimages.data.db.Database
import com.code.korti.seaofimages.data.models.Image
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.presentation.download.DownloadWorker
import com.code.korti.seaofimages.presentation.download.DownloadWorker.Companion.DOWNLOAD_WORK_ID
import java.util.concurrent.TimeUnit


class ImageRepository(private val context: Context) {

    private var imagesList: List<ImageFromList> = emptyList()
    private val imageDao = Database.instance.imageDao()

    suspend fun getImagesList(page: Int, numberOfImages: Int): List<ImageFromList> {
        val images = Networking.api.getImagesList(page, numberOfImages)
        saveImagesInDB(images)
        imagesList = imagesList + images
        return imagesList
    }

    suspend fun getImagesFromDB(): List<ImageFromList>{
        return imageDao.getImagesFromDB()
    }

    suspend fun searchImage(query: String, page: Int, numberOfImages: Int): List<ImageFromList>? {
        return Networking.api.searchImage(query, page, numberOfImages).images
    }

    suspend fun getImage(id: String): Image {
        return Networking.api.getImage(id)
    }

    suspend fun deleteAllFromDB() {
        imageDao.deleteAllFromDB()
    }

    suspend fun putLike(imageId: String) {
        Networking.api.putLike(imageId)
    }

    suspend fun deleteLike(imageId: String) {
        Networking.api.deleteLike(imageId)
    }

    suspend fun trackPhotoDownload(imageId: String) {
        Networking.api.trackPhotoDownload(imageId)
    }

    private suspend fun saveImagesInDB(images: List<ImageFromList>) {
        imageDao.insertImagesInDB(images)
    }

    fun isConnected(): Boolean {
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

    fun startDownload(urlToDownload: String) {

        val workData = workDataOf(
            DownloadWorker.DOWNLOAD_URL_KEY to urlToDownload
        )

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workData)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 20, TimeUnit.SECONDS)
            .setConstraints(workConstraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(DOWNLOAD_WORK_ID, ExistingWorkPolicy.KEEP, workRequest)
    }
}