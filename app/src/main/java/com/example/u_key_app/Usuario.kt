package com.example.u_key_app

data class Usuario(
    val id: Int,
    var nombre: String,
    var apellidos: String,
    var email: String,
    var password: String,
    var fotoPerfil: String? = null
)