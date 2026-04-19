package com.example.u_key_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
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

        // ✅ Cargar productos destacados aleatorios al inicio (como un fragment especial)
        loadFeaturedProductsFragment()

        // Configurar clics en categorías
        setupCategoryClicks()

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

    private fun loadFeaturedProductsFragment() {
        // ✅ Carga el fragment de productos destacados aleatorios
        val featuredFragment = ProductosFragment.newInstance("destacados") // ✅ Solo 1 parámetro
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, featuredFragment)
            .commit()
    }

    private fun setupCategoryClicks() {
        findViewById<LinearLayout>(R.id.categoryTeclados).setOnClickListener {
            loadProductsFragment("Teclados")
        }
        findViewById<LinearLayout>(R.id.categoryRatones).setOnClickListener {
            loadProductsFragment("Ratones")
        }
        findViewById<LinearLayout>(R.id.categoryAccesorios).setOnClickListener {
            loadProductsFragment("Accesorios")
        }
    }

    private fun loadProductsFragment(categoria: String) {
        // ✅ Carga el fragment de productos por categoría
        val fragment = ProductosFragment.newInstance(categoria) // ✅ Solo 1 parámetro
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // ✅ Eliminamos la función loadFeaturedProducts() porque ahora lo hace el fragment
    // private fun loadFeaturedProducts() { ... }

    // ✅ Eliminamos la función cargarImagenProducto() porque ahora la maneja el fragment
    // private fun cargarImagenProducto(...) { ... }

    // ✅ Eliminamos la función agregarAlCarrito() porque ahora la maneja el fragment
    // private fun agregarAlCarrito(...) { ... }
}