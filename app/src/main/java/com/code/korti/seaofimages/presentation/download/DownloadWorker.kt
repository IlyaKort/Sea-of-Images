package com.code.korti.seaofimages.presentation.download

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.code.korti.seaofimages.data.Networking
import timber.log.Timber
import java.sql.Timestamp

class DownloadWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private var result = 0

    override suspend fun doWork(): Result {
        val urlToDownload = inputData.getString(DOWNLOAD_URL_KEY)

        saveImage(urlToDownload!!)
        return when (result) {
            2 -> Result.success()
            3 -> Result.failure()
            else -> Result.retry()
        }
    }

    private suspend fun saveImage(url: String) {
        val imageUri = saveImageDetails(url)
        saveImage(url, imageUri)
        makeImageVisible(imageUri)
    }

    private fun saveImageDetails(url: String): Uri {
        val volume = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        } else {
            MediaStore.VOLUME_EXTERNAL
        }

        val imageUri = MediaStore.Images.Media.getContentUri(volume)
        val fileName = timestamp() + "_" + url.substring(url.lastIndexOf("/") + 1)
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, timestamp() + ".jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        return context.contentResolver.insert(imageUri, imageDetails)!!
    }

    private suspend fun saveImage(url: String, uri: Uri) {
        result = try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                Networking.api
                    .downloadImage(url)
                    .byteStream()
                    .use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
            }
            2
        } catch (t: Throwable) {
            Timber.d("error = $t")
            3
        }
    }

    private fun makeImageVisible(videoUri: Uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

        val videoDetails = ContentValues().apply {
            put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        context.contentResolver.update(videoUri, videoDetails, null, null)
    }

    private fun timestamp(): String {
        return Timestamp(System.currentTimeMillis()).time.toString()
    }

    companion object {
        const val DOWNLOAD_URL_KEY = "download url key"
        const val DOWNLOAD_WORK_ID = "download work"
    }
}