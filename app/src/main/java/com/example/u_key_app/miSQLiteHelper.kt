package com.example.u_key_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class miSQLiteHelper(context: Context) : SQLiteOpenHelper(context, "ukey.db", null, 1) {

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
}
