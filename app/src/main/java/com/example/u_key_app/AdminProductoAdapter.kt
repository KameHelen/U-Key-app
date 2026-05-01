package com.example.u_key_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminProductoAdapter(
    private val productos: MutableList<Producto>,
    private val onEliminar: (Producto) -> Unit
) : RecyclerView.Adapter<AdminProductoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvAdminNombreProducto)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvAdminPrecioProducto)
        val tvStock: TextView = itemView.findViewById(R.id.tvAdminStockProducto)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvAdminCategoriaProducto)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnAdminEliminarProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_producto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = String.format("%.2f €", producto.precio)
        holder.tvStock.text = "Stock: ${producto.stock}"
        holder.tvCategoria.text = "Cat. ID: ${producto.categoriaId}"
        holder.btnEliminar.setOnClickListener { onEliminar(producto) }
    }

    override fun getItemCount(): Int = productos.size
}
