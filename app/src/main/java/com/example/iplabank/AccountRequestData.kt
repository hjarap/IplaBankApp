package com.example.iplabank

data class AccountRequestData (
    val nombreCompleto: String,
    val rut: String,
    val fechaNacimiento: String,
    val email: String,
    val telefono: String,
    val frontIdImage: String?,
    val backIdImage: String?
)