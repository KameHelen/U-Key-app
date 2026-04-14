package com.example.u_key_app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CarritoActivity : AppCompatActivity() {

    private lateinit var dbHelper: miSQLiteHelper
    private lateinit var rvCarrito: RecyclerView
    private lateinit var adapter: CarritoAdapter
    private lateinit var tvTotal: TextView
    private lateinit var tvVacio: TextView
    private var usuarioId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        usuarioId = intent.getIntExtra("usuario_id", -1)
        dbHelper = miSQLiteHelper(this)

        rvCarrito = findViewById(R.id.rvCarrito)
        tvTotal = findViewById(R.id.tvTotal)
        tvVacio = findViewById(R.id.tvCarritoVacio)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizarCompra)

        btnBack.setOnClickListener { finish() }

        btnFinalizar.setOnClickListener {
            if (adapter.itemCount > 0) {
                // Aquí iría la lógica para procesar el pedido
                dbHelper.vaciarCarrito(usuarioId)
                cargarDatos()
                Toast.makeText(this, "¡Compra realizada con éxito!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }

        setupRecyclerView()
        cargarDatos()
    }

    private fun setupRecyclerView() {
        rvCarrito.layoutManager = LinearLayoutManager(this)
        adapter = CarritoAdapter(mutableListOf(), 
            onCantidadChanged = { item, nuevaCantidad ->
                dbHelper.actualizarCantidadCarrito(item.id, nuevaCantidad)
                cargarDatos()
            },
            onEliminar = { item ->
                dbHelper.eliminarDelCarrito(item.id)
                cargarDatos()
            }
        )
        rvCarrito.adapter = adapter
    }

    private fun cargarDatos() {
        val items = dbHelper.obtenerCarrito(usuarioId)
        adapter.updateData(items)

        if (items.isEmpty()) {
            tvVacio.visibility = View.VISIBLE
            rvCarrito.visibility = View.GONE
            tvTotal.text = "0.00 €"
        } else {
            tvVacio.visibility = View.GONE
            rvCarrito.visibility = View.VISIBLE
            
            var total = 0.0
            for (item in items) {
                total += item.precio * item.cantidad
            }
            tvTotal.text = String.format("%.2f €", total)
        }
    }
}
