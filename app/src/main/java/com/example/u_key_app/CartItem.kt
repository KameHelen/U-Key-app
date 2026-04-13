package com.example.u_key_app

data class CartItem(
    val id: Int,           // ID de la fila en la tabla carrito
    val productoId: Int,
    val nombre: String,
    val precio: Double,
    var cantidad: Int,
    val imagen: String?
)
