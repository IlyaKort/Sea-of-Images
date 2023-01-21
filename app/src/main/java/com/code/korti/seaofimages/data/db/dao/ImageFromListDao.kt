package com.code.korti.seaofimages.data.db.dao

import androidx.room.*
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.data.models.contract.ImageContract

@Dao
interface ImageFromListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImagesInDB(images: List<ImageFromList>)

    @Transaction
    @Query("SELECT * FROM ${ImageContract.TABLE_NAME}")
    suspend fun getImagesFromDB(): List<ImageFromList>

    @Query("DELETE FROM ${ImageContract.TABLE_NAME}")
    suspend fun deleteAllFromDB()
}