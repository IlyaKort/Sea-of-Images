package com.code.korti.seaofimages.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.code.korti.seaofimages.data.db.dao.CollectionDao
import com.code.korti.seaofimages.data.db.dao.ImageFromListDao
import com.code.korti.seaofimages.data.models.Collection
import com.code.korti.seaofimages.data.models.CollectionPhotos
import com.code.korti.seaofimages.data.models.ImageFromList

@Database(
    entities = [
        ImageFromList::class,
        Collection::class,
        CollectionPhotos::class
    ], version = MyDatabase.DB_VERSION
)
abstract class MyDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageFromListDao
    abstract fun collectionDao(): CollectionDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "my-database"
    }
}