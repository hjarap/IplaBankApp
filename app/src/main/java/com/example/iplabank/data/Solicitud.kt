package com.example.iplabank.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "solicitudes")
data class Solicitud(
    @PrimaryKey(autoGenerate = true) var id:Long = 0,
    var nombreCompleto: String,
    var rut:String,
    var fechaNacimiento: String,
    var email: String,
    var telefono: String,
    var latitud: Double,
    var longitud: Double,
    var frontIdImage:String?,
    var backIdImage:String?,
    var fechaSolicitud: LocalDate
)