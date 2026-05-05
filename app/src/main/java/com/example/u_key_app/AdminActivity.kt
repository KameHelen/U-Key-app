package com.example.u_key_app

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminActivity : AppCompatActivity() {

    private lateinit var dbHelper: miSQLiteHelper
    private lateinit var rvStock: RecyclerView
    private lateinit var adapter: StockAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        dbHelper = miSQLiteHelper(this)

        // Colorear los círculos de la leyenda
        pintarLeyenda()

        // Configurar RecyclerView
        rvStock = findViewById(R.id.rvStock)
        rvStock.layoutManager = LinearLayoutManager(this)

        val productos = dbHelper.obtenerTodosProductos().toMutableList()

        adapter = StockAdapter(productos) { producto, position, nuevoStock ->
            dbHelper.actualizarStock(producto.id, nuevoStock)
            adapter.actualizarStock(position, nuevoStock)
        }

        rvStock.adapter = adapter

        // Botón Salir → volver al Login
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    // Pinta los tres círculos de la leyenda con sus colores correspondientes
    private fun pintarLeyenda() {
        setCircleColor(R.id.legendVerde, "#4CAF50")
        setCircleColor(R.id.legendNaranja, "#FF9800")
        setCircleColor(R.id.legendRojo, "#F44336")
    }

    private fun setCircleColor(viewId: Int, colorHex: String) {
        val view = findViewById<android.view.View>(viewId)
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(colorHex))
        }
        view.background = drawable
    }
}
