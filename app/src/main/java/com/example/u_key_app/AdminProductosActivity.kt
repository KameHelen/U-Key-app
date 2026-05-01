package com.example.u_key_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminProductosActivity : AppCompatActivity() {

    private lateinit var dbHelper: miSQLiteHelper
    private lateinit var adapter: AdminProductoAdapter
    private val productos = mutableListOf<Producto>()

    private val addProductoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            cargarProductos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_productos)

        dbHelper = miSQLiteHelper(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val rvProductos = findViewById<RecyclerView>(R.id.rvAdminProductos)
        val fabAnadir = findViewById<FloatingActionButton>(R.id.fabAnadirProducto)

        btnBack.setOnClickListener { finish() }

        adapter = AdminProductoAdapter(productos) { producto ->
            AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Seguro que quieres eliminar \"${producto.nombre}\"?")
                .setPositiveButton("Eliminar") { _, _ ->
                    dbHelper.eliminarProducto(producto.id)
                    cargarProductos()
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        rvProductos.layoutManager = LinearLayoutManager(this)
        rvProductos.adapter = adapter

        fabAnadir.setOnClickListener {
            addProductoLauncher.launch(Intent(this, AdminAddProductoActivity::class.java))
        }

        cargarProductos()
    }

    override fun onResume() {
        super.onResume()
        cargarProductos()
    }

    private fun cargarProductos() {
        productos.clear()
        productos.addAll(dbHelper.obtenerTodosLosProductos())
        adapter.notifyDataSetChanged()
        val tvContador = findViewById<TextView>(R.id.tvContadorProductos)
        tvContador.text = "${productos.size} productos"
    }
}
