package com.example.u_key_app

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class AdminAddProductoActivity : AppCompatActivity() {

    private lateinit var dbHelper: miSQLiteHelper
    private var categorias = listOf<Pair<Int, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_producto)

        dbHelper = miSQLiteHelper(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val etNombre = findViewById<TextInputEditText>(R.id.etAdminNombre)
        val etDescripcion = findViewById<TextInputEditText>(R.id.etAdminDescripcion)
        val etPrecio = findViewById<TextInputEditText>(R.id.etAdminPrecio)
        val etStock = findViewById<TextInputEditText>(R.id.etAdminStock)
        val etImagen = findViewById<TextInputEditText>(R.id.etAdminImagen)
        val spinnerCategoria = findViewById<Spinner>(R.id.spinnerCategoria)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarProducto)

        btnBack.setOnClickListener { finish() }

        categorias = dbHelper.obtenerCategorias()
        val nombresCategoria = categorias.map { it.second }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresCategoria)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = spinnerAdapter

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val precioStr = etPrecio.text.toString().trim()
            val stockStr = etStock.text.toString().trim()
            val imagen = etImagen.text.toString().trim()

            if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val precio = precioStr.toDoubleOrNull()
            val stock = stockStr.toIntOrNull()
            if (precio == null || stock == null) {
                Toast.makeText(this, "Precio o stock no válidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoriaSeleccionada = categorias.getOrNull(spinnerCategoria.selectedItemPosition)
            if (categoriaSeleccionada == null) {
                Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.añadirProducto(nombre, descripcion, precio, stock, imagen, categoriaSeleccionada.first)
            if (result != -1L) {
                Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error al añadir el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
