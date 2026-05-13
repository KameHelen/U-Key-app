package com.example.u_key_app

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminActivity : AppCompatActivity() {

    private lateinit var dbHelper: miSQLiteHelper
    private lateinit var rvStock: RecyclerView
    private lateinit var adapter: StockAdapter

    private var fotoProductoUri: Uri? = null
    private var ivPreviewActual: ImageView? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            fotoProductoUri = it
            ivPreviewActual?.setImageURI(it)
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        dbHelper = miSQLiteHelper(this)

        // Colorear los círculos de la leyenda
        pintarLeyenda()

        // Configurar RecyclerView
        rvStock = findViewById(R.id.rvStock)
        rvStock.layoutManager = LinearLayoutManager(this)

        cargarProductos()

        // Botón Añadir Producto
        findViewById<Button>(R.id.btnAddProduct).setOnClickListener {
            mostrarDialogoAñadir()
        }

        // Botón Salir → volver al Login
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun cargarProductos() {
        val productos = dbHelper.obtenerTodosProductos().toMutableList()

        adapter = StockAdapter(
            productos = productos,
            onStockChanged = { producto, position, nuevoStock ->
                dbHelper.actualizarStock(producto.id, nuevoStock)
                adapter.actualizarStock(position, nuevoStock)
            },
            onDelete = { producto, position ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar producto")
                    .setMessage("¿Estás seguro de que quieres eliminar '${producto.nombre}'?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        dbHelper.eliminarProducto(producto.id)
                        adapter.eliminarProducto(position)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        rvStock.adapter = adapter
    }

    private fun mostrarDialogoAñadir() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)
        val etStock = dialogView.findViewById<EditText>(R.id.etStock)
        val etCatId = dialogView.findViewById<EditText>(R.id.etCategoriaId)
        val ivPreview = dialogView.findViewById<ImageView>(R.id.ivProductoPreview)
        val btnFoto = dialogView.findViewById<Button>(R.id.btnSeleccionarImagen)

        ivPreviewActual = ivPreview
        fotoProductoUri = null // Resetear para el nuevo diálogo

        btnFoto.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Añadir") { _, _ ->
                val nombre = etNombre.text.toString()
                val desc = etDesc.text.toString()
                val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                val stock = etStock.text.toString().toIntOrNull() ?: 0
                val catId = etCatId.text.toString().toIntOrNull() ?: 1
                val imagen = fotoProductoUri?.toString() ?: "ic_producto_placeholder"

                if (nombre.isNotEmpty()) {
                    dbHelper.insertarProducto(nombre, desc, precio, stock, imagen, catId)
                    // Recargar la lista
                    cargarProductos()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
