package com.example.u_key_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarritoAdapter(
    private var items: MutableList<CartItem>,
    private val onCantidadChanged: (CartItem, Int) -> Unit,
    private val onEliminar: (CartItem) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProducto: ImageView = view.findViewById(R.id.ivProducto)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreProducto)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecioProducto)
        val tvCantidad: TextView = view.findViewById(R.id.tvCantidad)
        val btnRestar: ImageButton = view.findViewById(R.id.btnRestar)
        val btnSumar: ImageButton = view.findViewById(R.id.btnSumar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvNombre.text = item.nombre
        holder.tvPrecio.text = String.format("%.2f €", item.precio)
        holder.tvCantidad.text = item.cantidad.toString()

        // Cargar imagen del producto
        val ctx = holder.itemView.context
        val resId = if (!item.imagen.isNullOrEmpty())
            ctx.resources.getIdentifier(item.imagen, "drawable", ctx.packageName)
        else 0
        if (resId != 0) holder.ivProducto.setImageResource(resId)
        else holder.ivProducto.setImageResource(R.drawable.ic_producto_placeholder)

        holder.btnSumar.setOnClickListener {
            onCantidadChanged(item, item.cantidad + 1)
        }

        holder.btnRestar.setOnClickListener {
            if (item.cantidad > 1) {
                onCantidadChanged(item, item.cantidad - 1)
            }
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(item)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<CartItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
