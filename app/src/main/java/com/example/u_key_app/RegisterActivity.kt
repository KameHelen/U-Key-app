package com.example.u_key_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: miSQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = miSQLiteHelper(this)

        // Soporte edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val etNombre = findViewById<EditText>(R.id.editTextNombre)
        val etApellidos = findViewById<EditText>(R.id.editTextApellidos)
        val etCorreo = findViewById<EditText>(R.id.editTextCorreo)
        val etPassword = findViewById<EditText>(R.id.editTextPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)

        buttonBack.setOnClickListener {
            finish()
        }

        buttonRegister.setOnClickListener {
            val nombre = etNombre.text.toString()
            val apellidos = etApellidos.text.toString()
            val correo = etCorreo.text.toString()
            val pass = etPassword.text.toString()
            val confirmPass = etConfirmPassword.text.toString()

            if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else if (pass != confirmPass) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                val result = dbHelper.registrarUsuario(nombre, apellidos, correo, pass)
                if (result != -1L) {
                    Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar: El email ya existe", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
