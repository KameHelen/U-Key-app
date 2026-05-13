package com.example.u_key_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class PerfilActivity : AppCompatActivity() {

    private lateinit var dbHelper: miSQLiteHelper
    private var usuarioId = -1
    private var fotoUri: Uri? = null

    private lateinit var ivPerfil: ImageView
    private lateinit var etNombre: TextInputEditText
    private lateinit var etApellidos: TextInputEditText
    private lateinit var etEmail: TextInputEditText

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            fotoUri = it
            ivPerfil.setImageURI(it)
            // Otorgar permisos persistentes para la URI (si es posible)
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        dbHelper = miSQLiteHelper(this)
        usuarioId = intent.getIntExtra("usuario_id", -1)

        ivPerfil = findViewById(R.id.ivPerfilGrande)
        etNombre = findViewById(R.id.etNombrePerfil)
        etApellidos = findViewById(R.id.etApellidosPerfil)
        etEmail = findViewById(R.id.etEmailPerfil)

        cargarDatos()

        findViewById<Button>(R.id.btnCambiarFoto).setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.btnGuardarPerfil).setOnClickListener {
            guardarCambios()
        }

        findViewById<Button>(R.id.btnVolverPerfil).setOnClickListener {
            finish()
        }
    }

    private fun cargarDatos() {
        if (usuarioId != -1) {
            val usuario = dbHelper.obtenerUsuario(usuarioId)
            usuario?.let {
                etNombre.setText(it.nombre)
                etApellidos.setText(it.apellidos)
                etEmail.setText(it.email)
                it.fotoPerfil?.let { uriStr ->
                    fotoUri = Uri.parse(uriStr)
                    ivPerfil.setImageURI(fotoUri)
                }
            }
        }
    }

    private fun guardarCambios() {
        val nombre = etNombre.text.toString().trim()
        val apellidos = etApellidos.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val res = dbHelper.actualizarUsuario(usuarioId, nombre, apellidos, email, fotoUri?.toString())
        if (res > 0) {
            Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
        }
    }
}