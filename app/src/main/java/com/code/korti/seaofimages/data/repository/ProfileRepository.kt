package com.code.korti.seaofimages.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.code.korti.seaofimages.data.Networking
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.data.models.Profile

class ProfileRepository(private val context: Context) {

    private var imagesFromList: List<ImageFromList> = emptyList()

    suspend fun getMyProfile(): Profile {
        return Networking.api.getMyProfile()
    }

    suspend fun getImagesList(
        username: String,
        page: Int,
        numberOfImages: Int
    ): List<ImageFromList> {
        val images = Networking.api.getListOfLikedPhotos(username, page, numberOfImages)
        imagesFromList = (imagesFromList + images)
        return imagesFromList.distinctBy { it.id }
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
}