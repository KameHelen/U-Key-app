package com.example.u_key_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class miSQLiteHelper(context: Context) : SQLiteOpenHelper(context, "ukey.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Tabla Categorías
        val createCategorias = ("CREATE TABLE categorias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "descripcion TEXT)")
        db?.execSQL(createCategorias)

        // Tabla Usuarios
        val createUsuarios = ("CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "apellidos TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)")
        db?.execSQL(createUsuarios)

        // Tabla Productos
        val createProductos = ("CREATE TABLE productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "descripcion TEXT, " +
                "precio REAL, " +
                "stock INTEGER, " +
                "imagen TEXT, " +
                "categoria_id INTEGER, " +
                "FOREIGN KEY(categoria_id) REFERENCES categorias(id))")
        db?.execSQL(createProductos)

        // Tabla Carrito
        val createCarrito = ("CREATE TABLE carrito (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario_id INTEGER, " +
                "producto_id INTEGER, " +
                "cantidad INTEGER, " +
                "FOREIGN KEY(usuario_id) REFERENCES usuarios(id), " +
                "FOREIGN KEY(producto_id) REFERENCES productos(id))")
        db?.execSQL(createCarrito)

        // Insertar categorías iniciales
        insertarCategoriasIniciales(db)
    }

    private fun insertarCategoriasIniciales(db: SQLiteDatabase?) {
        val categorias = arrayOf(
            arrayOf("Teclados", "Teclados mecánicos y de membrana"),
            arrayOf("Ratones", "Ratones gaming y de oficina"),
            arrayOf("Accesorios", "Alfombrillas, cables y más")
        )

        for (cat in categorias) {
            val values = ContentValues().apply {
                put("nombre", cat[0])
                put("descripcion", cat[1])
            }
            db?.insert("categorias", null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS carrito")
        db?.execSQL("DROP TABLE IF EXISTS productos")
        db?.execSQL("DROP TABLE IF EXISTS usuarios")
        db?.execSQL("DROP TABLE IF EXISTS categorias")
        onCreate(db)
    }

    // --- Métodos para Usuarios ---

    fun registrarUsuario(nombre: String, apellidos: String, email: String, pass: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("apellidos", apellidos)
            put("email", email)
            put("password", pass)
        }
        val result = db.insert("usuarios", null, values)
        db.close()
        return result
    }

    fun verificarUsuario(email: String, pass: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE email = ? AND password = ?",
            arrayOf(email, pass)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // --- Métodos para el Carrito ---

    fun obtenerCarrito(usuarioId: Int): List<CartItem> {
        val lista = mutableListOf<CartItem>()
        val db = this.readableDatabase
        val query = """
            SELECT c.id, p.nombre, p.precio, c.cantidad, p.imagen, p.id as producto_id
            FROM carrito c
            JOIN productos p ON c.producto_id = p.id
            WHERE c.usuario_id = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(usuarioId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val item = CartItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("producto_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                    cursor.getString(cursor.getColumnIndexOrThrow("imagen"))
                )
                lista.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun agregarAlCarrito(usuarioId: Int, productoId: Int, cantidad: Int): Long {
        val db = this.writableDatabase
        
        // Verificar si ya existe
        val cursor = db.rawQuery(
            "SELECT id, cantidad FROM carrito WHERE usuario_id = ? AND producto_id = ?",
            arrayOf(usuarioId.toString(), productoId.toString())
        )
        
        var result: Long
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val cantActual = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
            val values = ContentValues().apply {
                put("cantidad", cantActual + cantidad)
            }
            result = db.update("carrito", values, "id = ?", arrayOf(id.toString())).toLong()
        } else {
            val values = ContentValues().apply {
                put("usuario_id", usuarioId)
                put("producto_id", productoId)
                put("cantidad", cantidad)
            }
            result = db.insert("carrito", null, values)
        }
        cursor.close()
        db.close()
        return result
    }

    fun eliminarDelCarrito(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete("carrito", "id = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun actualizarCantidadCarrito(id: Int, cantidad: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("cantidad", cantidad)
        }
        val result = db.update("carrito", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun vaciarCarrito(usuarioId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete("carrito", "usuario_id = ?", arrayOf(usuarioId.toString()))
        db.close()
        return result
    }
}
