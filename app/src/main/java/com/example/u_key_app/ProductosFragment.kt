package com.example.u_key_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProductosFragment : Fragment() {

    private lateinit var dbHelper: miSQLiteHelper
    private var usuarioId = -1
    private var categoria = ""

    companion object {
        fun newInstance(categoria: String): ProductosFragment {
            val fragment = ProductosFragment()
            val args = Bundle()
            args.putString("categoria", categoria)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflamos el layout del fragment (el que tiene las 4 tarjetas)
        return inflater.inflate(R.layout.fragment_productos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos dbHelper aquí
        dbHelper = miSQLiteHelper(requireContext())
        usuarioId = requireActivity().intent.getIntExtra("usuario_id", -1)
        categoria = arguments?.getString("categoria") ?: ""

        // Cargar productos según la categoría recibida
        loadProductsByCategory()
    }

    private fun loadProductsByCategory() {
        // Limpiar vistas previas
        limpiarTarjetas()

        var productos = listOf<Producto>()

        if (categoria.lowercase() == "destacados") {
            productos = dbHelper.obtenerProductosDestacadosAleatorios().take(4)
        } else {
            productos = dbHelper.obtenerProductosPorCategoria(categoria).take(4)
        }

        if (productos.isEmpty()) {
            view?.findViewById<TextView>(R.id.tvMensajeSinProductos)?.apply {
                visibility = View.VISIBLE
                text = "No hay productos en esta categoría."
            }
            return
        }

        // Mostrar productos en las tarjetas — usando `requireView()` (seguro, no null)
        val v = requireView()

        for ((index, producto) in productos.withIndex()) {
            when (index) {
                0 -> mostrarProductoEnCard(v, R.id.cardProducto1, R.id.ivProducto1, R.id.tvNombreProducto1, R.id.tvDescProducto1, R.id.tvPrecioProducto1, R.id.btnAnadirProducto1, producto)
                1 -> mostrarProductoEnCard(v, R.id.cardProducto2, R.id.ivProducto2, R.id.tvNombreProducto2, R.id.tvDescProducto2, R.id.tvPrecioProducto2, R.id.btnAnadirProducto2, producto)
                2 -> mostrarProductoEnCard(v, R.id.cardProducto3, R.id.ivProducto3, R.id.tvNombreProducto3, R.id.tvDescProducto3, R.id.tvPrecioProducto3, R.id.btnAnadirProducto3, producto)
                3 -> mostrarProductoEnCard(v, R.id.cardProducto4, R.id.ivProducto4, R.id.tvNombreProducto4, R.id.tvDescProducto4, R.id.tvPrecioProducto4, R.id.btnAnadirProducto4, producto)
            }
        }
    }

    private fun limpiarTarjetas() {
        // Ocultar las tarjetas o dejarlas en blanco si no hay suficientes productos
        view?.findViewById<View>(R.id.cardProducto1)?.visibility = View.GONE
        view?.findViewById<View>(R.id.cardProducto2)?.visibility = View.GONE
        view?.findViewById<View>(R.id.cardProducto3)?.visibility = View.GONE
        view?.findViewById<View>(R.id.cardProducto4)?.visibility = View.GONE
    }

    private fun mostrarProductoEnCard(view: View, cardId: Int, ivId: Int, tvNombreId: Int, tvDescId: Int, tvPrecioId: Int, btnId: Int, producto: Producto) {
        view.findViewById<View>(cardId).visibility = View.VISIBLE // Asegurar visibilidad
        view.findViewById<TextView>(tvNombreId).text = producto.nombre
        view.findViewById<TextView>(tvDescId).text = producto.descripcion
        view.findViewById<TextView>(tvPrecioId).text = String.format("%.2f €", producto.precio)

        cargarImagenProducto(producto.imagen, view.findViewById(ivId))

        view.findViewById<Button>(btnId).setOnClickListener {
            agregarAlCarrito(producto.id)
        }
    }

    private fun cargarImagenProducto(nombreImagen: String?, imageView: ImageView) {
        if (!nombreImagen.isNullOrEmpty()) {
            val resId = resources.getIdentifier(nombreImagen, "drawable", requireContext().packageName)
            if (resId != 0) {
                imageView.setImageResource(resId)
                return
            }
        }
        imageView.setImageResource(R.drawable.ic_producto_placeholder)
    }

    private fun agregarAlCarrito(productoId: Int) {
        if (usuarioId == -1) {
            Toast.makeText(context, "Error: sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }
        val resultado = dbHelper.agregarAlCarrito(usuarioId, productoId, 1)
        if (resultado != -1L) {
            Toast.makeText(context, "Producto añadido al carrito ✓", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error al añadir al carrito", Toast.LENGTH_SHORT).show()
        }
    }
}