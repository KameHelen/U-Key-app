package com.example.u_key_app

data class ProductoAdmin(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    var stock: Int,          // var para poder modificarlo sin recrear el objeto
    val imagen: String?,
    val categoriaId: Int,
    val categoriaNombre: String
)
