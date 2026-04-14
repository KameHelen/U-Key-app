package com.example.u_key_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var dbHelper: miSQLiteHelper
    private var usuarioId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        usuarioId = intent.getIntExtra("usuario_id", -1)
        dbHelper = miSQLiteHelper(this)

        // Cargar y mostrar productos destacados
        val productos = dbHelper.obtenerProductosDestacados()

        if (productos.isNotEmpty()) {
            val p1 = productos[0]
            findViewById<TextView>(R.id.tvNombreProducto1).text = p1.nombre
            findViewById<TextView>(R.id.tvDescProducto1).text = p1.descripcion
            findViewById<TextView>(R.id.tvPrecioProducto1).text = String.format("%.2f €", p1.precio)
            cargarImagenProducto(p1.imagen, findViewById(R.id.ivProducto1))

            findViewById<Button>(R.id.btnAnadirProducto1).setOnClickListener {
                agregarAlCarrito(p1.id)
            }
        }

        if (productos.size >= 2) {
            val p2 = productos[1]
            findViewById<TextView>(R.id.tvNombreProducto2).text = p2.nombre
            findViewById<TextView>(R.id.tvDescProducto2).text = p2.descripcion
            findViewById<TextView>(R.id.tvPrecioProducto2).text = String.format("%.2f €", p2.precio)
            cargarImagenProducto(p2.imagen, findViewById(R.id.ivProducto2))

            findViewById<Button>(R.id.btnAnadirProducto2).setOnClickListener {
                agregarAlCarrito(p2.id)
            }
        }

        // Botón carrito
        findViewById<ImageButton>(R.id.btnCarrito).setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
        }

        // Botón Salir → volver al Login
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun cargarImagenProducto(nombreImagen: String?, imageView: ImageView) {
        if (!nombreImagen.isNullOrEmpty()) {
            val resId = resources.getIdentifier(nombreImagen, "drawable", packageName)
            if (resId != 0) {
                imageView.setImageResource(resId)
                return
            }
        }
        imageView.setImageResource(R.drawable.ic_producto_placeholder)
    }

    private fun agregarAlCarrito(productoId: Int) {
        if (usuarioId == -1) {
            Toast.makeText(this, "Error: sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }
        val resultado = dbHelper.agregarAlCarrito(usuarioId, productoId, 1)
        if (resultado != -1L) {
            Toast.makeText(this, "Producto añadido al carrito ✓", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al añadir al carrito", Toast.LENGTH_SHORT).show()
        }
    }
}