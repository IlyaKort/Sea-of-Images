package com.code.korti.seaofimages.data.db.dao

import androidx.room.*
import com.code.korti.seaofimages.data.models.Collection
import com.code.korti.seaofimages.data.models.CollectionPhotos
import com.code.korti.seaofimages.data.models.CollectionWithPhotos
import com.code.korti.seaofimages.data.models.contract.CollectionContract
import com.code.korti.seaofimages.data.models.contract.CollectionPhotosContract

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionsInDB(collections: List<Collection>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionPhotosInDB(photos: List<CollectionPhotos>)

    @Transaction
    @Query("SELECT * FROM ${CollectionContract.TABLE_NAME}")
    suspend fun getCollectionsFromDB(): List<Collection>

    @Query("DELETE FROM ${CollectionContract.TABLE_NAME}")
    suspend fun deleteAllCollectionsFromDB()

    @Query("DELETE FROM ${CollectionPhotosContract.TABLE_NAME}")
    suspend fun deleteAllCollectionPhotosFromDB()

    @Transaction
    @Query("SELECT * FROM ${CollectionContract.TABLE_NAME} WHERE ${CollectionContract.Columns.ID} = :collectionId")
    suspend fun getCollectionWithPhotosFromDB(collectionId: String): CollectionWithPhotos
}