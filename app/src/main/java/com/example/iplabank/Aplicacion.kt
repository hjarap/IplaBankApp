package com.example.iplabank

import android.app.Application
import com.example.iplabank.data.BaseDatos
import com.example.iplabank.data.SolicitudDao

class Aplicacion: Application() {

    companion object {
        lateinit var INSTANCE: Aplicacion
            private set
    }

    val solicitudDao: SolicitudDao by lazy {
        val database = BaseDatos.getDatabase(Aplicacion.INSTANCE)
        database.solicitudDao()
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

}