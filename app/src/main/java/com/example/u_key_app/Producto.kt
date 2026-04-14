package com.example.u_key_app

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagen: String?,
    val categoriaId: Int
)
