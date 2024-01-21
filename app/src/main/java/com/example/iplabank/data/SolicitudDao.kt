package com.example.iplabank.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface SolicitudDao {

    @Query("SELECT * FROM solicitudes ORDER BY fechaSolicitud DESC")
    suspend fun obtenerTodos(): List<Solicitud>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerPorId(id:Long): Solicitud

    @Query("SELECT * FROM solicitudes ORDER BY id DESC LIMIT 1")
    suspend fun obtenerUltimaSolicitud(): Solicitud?

    @Insert
    suspend fun insertar(solicitud:Solicitud)

    @Update
    suspend fun actualizar(solicitud:Solicitud)

    @Delete
    suspend fun eliminar(solicitud:Solicitud)
}