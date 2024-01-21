package com.example.iplabank.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Solicitud::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateConverter::class)
abstract class BaseDatos  : RoomDatabase(){
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        private var INSTANCE: BaseDatos? = null

        fun getDatabase(application: Application): BaseDatos {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    application,
                    BaseDatos::class.java,
                    "solicitud_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}