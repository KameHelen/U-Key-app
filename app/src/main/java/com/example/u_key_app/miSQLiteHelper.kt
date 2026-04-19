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
        // Insertar productos iniciales
        insertarProductosIniciales(db)
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

    private fun insertarProductosIniciales(db: SQLiteDatabase?) {
        val productos = arrayOf(
            arrayOf("Teclado Mecánico Pro", "Teclado mecánico RGB con switches Cherry MX Red. Respuesta ultrarrápida para gaming.", "89.99", "15", "1", "producto_teclado"),
            arrayOf("Ratón Gaming RGB", "Ratón gaming con sensor óptico de 16000 DPI y 7 botones programables.", "49.99", "30", "2", "producto_raton")
        )
        for (p in productos) {
            val values = ContentValues().apply {
                put("nombre", p[0])
                put("descripcion", p[1])
                put("precio", p[2].toDouble())
                put("stock", p[3].toInt())
                put("categoria_id", p[4].toInt())
                put("imagen", p[5])
            }
            db?.insert("productos", null, values)
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
            "SELECT id FROM usuarios WHERE email = ? AND password = ?",
            arrayOf(email, pass)
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun obtenerIdUsuario(email: String, pass: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM usuarios WHERE email = ? AND password = ?",
            arrayOf(email, pass)
        )
        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        }
        cursor.close()
        db.close()
        return id
    }

    // --- Métodos para Productos ---

    fun obtenerProductosDestacados(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = this.readableDatabase
        // Limitamos a 2 productos para destacados
        val cursor = db.rawQuery(
            "SELECT id, nombre, descripcion, precio, stock, imagen, categoria_id FROM productos LIMIT 2",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Producto(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imagen")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("categoria_id"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // ✅ Nuevo método: Obtener productos destacados aleatorios
    fun obtenerProductosDestacadosAleatorios(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = this.readableDatabase
        // Usamos ORDER BY RANDOM() y limitamos a 4
        val cursor = db.rawQuery(
            "SELECT id, nombre, descripcion, precio, stock, imagen, categoria_id FROM productos ORDER BY RANDOM() LIMIT 4",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Producto(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imagen")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("categoria_id"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // ... resto de métodos ...
    // Método: Obtener productos por nombre de categoría
    fun obtenerProductosPorCategoria(nombreCategoria: String): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = this.readableDatabase

        // Primero obtenemos el id de la categoría
        val cursorCat = db.rawQuery(
            "SELECT id FROM categorias WHERE nombre = ?",
            arrayOf(nombreCategoria)
        )

        var categoriaId = -1
        if (cursorCat.moveToFirst()) {
            categoriaId = cursorCat.getInt(cursorCat.getColumnIndexOrThrow("id"))
        }
        cursorCat.close()

        if (categoriaId != -1) {
            // Si encontramos la categoría, buscamos los productos
            val cursorProd = db.rawQuery(
                "SELECT id, nombre, descripcion, precio, stock, imagen, categoria_id FROM productos WHERE categoria_id = ? ORDER BY RANDOM() LIMIT 4",
                arrayOf(categoriaId.toString())
            )

            if (cursorProd.moveToFirst()) {
                do {
                    lista.add(
                        Producto(
                            cursorProd.getInt(cursorProd.getColumnIndexOrThrow("id")),
                            cursorProd.getString(cursorProd.getColumnIndexOrThrow("nombre")),
                            cursorProd.getString(cursorProd.getColumnIndexOrThrow("descripcion")),
                            cursorProd.getDouble(cursorProd.getColumnIndexOrThrow("precio")),
                            cursorProd.getInt(cursorProd.getColumnIndexOrThrow("stock")),
                            cursorProd.getString(cursorProd.getColumnIndexOrThrow("imagen")),
                            cursorProd.getInt(cursorProd.getColumnIndexOrThrow("categoria_id"))
                        )
                    )
                } while (cursorProd.moveToNext())
            }
            cursorProd.close()
        }

        db.close()
        return lista
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
