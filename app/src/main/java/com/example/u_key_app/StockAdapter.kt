package com.example.u_key_app

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StockAdapter(
    private val productos: MutableList<ProductoAdmin>,
    private val onStockChanged: (ProductoAdmin, Int, Int) -> Unit
) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProducto: ImageView = view.findViewById(R.id.ivProducto)
        val viewPrioridad: View = view.findViewById(R.id.viewPrioridad)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreProducto)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        val tvStock: TextView = view.findViewById(R.id.tvStock)
        val etStock: EditText = view.findViewById(R.id.etStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvNombre.text = producto.nombre
        holder.tvCategoria.text = producto.categoriaNombre
        holder.tvStock.text = "Stock: ${producto.stock} ud."

        // Imagen del producto
        val context = holder.itemView.context
        val resId = producto.imagen?.let {
            context.resources.getIdentifier(it, "drawable", context.packageName)
        } ?: 0
        if (resId != 0) {
            holder.ivProducto.setImageResource(resId)
        } else {
            holder.ivProducto.setImageResource(R.drawable.ic_producto_placeholder)
        }

        // Color del círculo según nivel de stock
        actualizarCirculoPrioridad(holder.viewPrioridad, producto.stock)

        // EditText: establecer valor actual sin disparar listeners
        holder.etStock.setOnFocusChangeListener(null)
        holder.etStock.setText(producto.stock.toString())

        // Guardar al perder el foco
        holder.etStock.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) guardarNuevoStock(holder)
        }

        // Guardar al pulsar "Hecho" en el teclado
        holder.etStock.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                holder.etStock.clearFocus()
                true
            } else false
        }


    }

    private fun guardarNuevoStock(holder: ViewHolder) {
        val pos = holder.bindingAdapterPosition
        if (pos == RecyclerView.NO_POSITION) return
        val nuevoStock = holder.etStock.text.toString().toIntOrNull()
        if (nuevoStock == null || nuevoStock < 0) {
            // Valor inválido: restaurar el valor actual
            holder.etStock.setText(productos[pos].stock.toString())
            return
        }
        if (nuevoStock != productos[pos].stock) {
            onStockChanged(productos[pos], pos, nuevoStock)
        }
    }

    private fun actualizarCirculoPrioridad(view: View, stock: Int) {
        val colorHex = when {
            stock > 10 -> "#4CAF50"   // Verde
            stock > 5  -> "#FF9800"   // Naranja
            else       -> "#F44336"   // Rojo
        }
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(colorHex))
        }
        view.background = drawable
    }

    // Actualiza el stock de un ítem y redibuja solo esa fila
    fun actualizarStock(position: Int, nuevoStock: Int) {
        if (position in productos.indices) {
            productos[position].stock = nuevoStock
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = productos.size
}
