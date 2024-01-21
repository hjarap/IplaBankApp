package com.example.iplabank.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.iplabank.Aplicacion
import com.example.iplabank.data.Solicitud
import com.example.iplabank.data.SolicitudDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AplicacionVM(application: Application) : AndroidViewModel(application) {

    private val solicitudDao: SolicitudDao = (application as? Aplicacion)?.solicitudDao
        ?: throw IllegalStateException("Application class must extend Aplicacion and provide a valid SolicitudDao.")

    var solicitudes by mutableStateOf(listOf<Solicitud>())

    fun insertarSolicitud(solicitud: Solicitud) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                solicitudDao.insertar(solicitud)
                obtenerDatos()
            } catch (e: Exception) {

            }
        }
    }

    suspend fun obtenerUltimaSolicitud(): Solicitud? {
        return withContext(Dispatchers.IO) {
            return@withContext solicitudDao.obtenerUltimaSolicitud()
        }
    }

    fun obtenerDatos() {
        viewModelScope.launch(Dispatchers.IO) {
            solicitudes = solicitudDao.obtenerTodos()
        }
    }

    fun actualizarSolicitud(solicitud: Solicitud) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                solicitudDao.actualizar(solicitud)
                obtenerDatos()
            } catch (e: Exception) {
                var mensaje = "Error en los datos."
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            ViewModelProvider.AndroidViewModelFactory.getInstance(Aplicacion.INSTANCE)
    }
}

