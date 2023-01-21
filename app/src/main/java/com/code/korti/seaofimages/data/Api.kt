package com.code.korti.seaofimages.data

import com.code.korti.seaofimages.data.models.*
import com.code.korti.seaofimages.data.models.Collection
import okhttp3.ResponseBody
import retrofit2.http.*

interface Api {

    @GET("me")
    suspend fun getMyProfile(): Profile

    @GET("photos")
    suspend fun getImagesList(
        @Query("page")
        page: Int,
        @Query("per_page")
        numberOfImages: Int
    ): List<ImageFromList>

    @GET("collections")
    suspend fun getCollectionsList(
        @Query("page")
        page: Int,
        @Query("per_page")
        numberOfCollection: Int
    ): List<Collection>

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id")
        collectionId: String,
        @Query("page")
        page: Int,
        @Query("per_page")
        numberOfPhotos: Int
    ): List<ImageFromList>

    @GET("photos/{id}")
    suspend fun getImage(
        @Path("id")
        imageId: String
    ): Image

    @GET("search/photos")
    suspend fun searchImage(
        @Query("query")
        query: String,
        @Query("page")
        page: Int,
        @Query("per_page")
        numberOfImages: Int
    ): SearchImageResults

    @GET("users/{username}/likes")
    suspend fun getListOfLikedPhotos(
        @Path("username")
        username: String,
        @Query("page")
        page: Int,
        @Query("per_page")
        numberOfImages: Int
    ): List<ImageFromList>

    @POST("photos/{id}/like")
    suspend fun putLike(@Path("id") imageId: String)

    @DELETE("photos/{id}/like")
    suspend fun deleteLike(@Path("id") imageId: String)

    //скачать фото
    @GET
    suspend fun downloadImage(
        @Url url: String
    ): ResponseBody

    @GET("photos/{id}/download")
    suspend fun trackPhotoDownload(@Path("id") imageId: String)
}