package com.example.proyemergentes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyemergentes.Intrface.RetrofitCliente
import com.example.proyemergentes.dataclass.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: RegisterActivity
    var users = Users("", "", "","")

    private lateinit var textuser: EditText
    private lateinit var textpass: EditText
    private lateinit var textemail: EditText
    private lateinit var textRepeatPassword: EditText
    private lateinit var btnRegistrar: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        textuser = findViewById(R.id.editTextUsername)
        textpass = findViewById(R.id.editTextPassword)
        textemail = findViewById(R.id.editTextEmail)
        textRepeatPassword = findViewById(R.id.editTextRepeatPassword)
        btnRegistrar = findViewById(R.id.buttonRegister)

        btnRegistrar.setOnClickListener {
            if (validateInputs()) {
                this.users.Usuario = textuser.text.toString()
                this.users.Password = textpass.text.toString()
                this.users.Email = textemail.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val call = RetrofitCliente.webService.agregarUser(users)
                    runOnUiThread {
                        if (call.isSuccessful) {
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@RegisterActivity, "Error en el registro", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        val textButton = findViewById<TextView>(R.id.textButton)
        textButton.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (!validateUsername() && !validateEmail() && !validatePassword() && !validateRepeatPassword()) {
            isValid = false
            textuser.requestFocus()
            textemail.requestFocus()
            textpass.requestFocus()
            textRepeatPassword.requestFocus()
        } else if (!validateEmail()) {
            isValid = false
            textemail.requestFocus()
        } else if (!validatePassword()) {
            isValid = false
            textpass.requestFocus()
        } else if (!validateRepeatPassword()) {
            isValid = false
            textRepeatPassword.requestFocus()
        }

        return isValid
    }

    private fun validateUsername(): Boolean {
        val username = textuser.text.toString().trim()
        return if (TextUtils.isEmpty(username)) {
            textuser.error = "Username es requerido"
            false
        } else {
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email = textemail.text.toString().trim()
        return if (TextUtils.isEmpty(email)) {
            textemail.error = "Email es requerido"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textemail.error = "Ingrese un email válido"
            false
        } else {
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = textpass.text.toString().trim()
        return if (TextUtils.isEmpty(password)) {
            textpass.error = "Password es requerido"
            false
        } else if (password.length < 6) {
            textpass.error = "Password debe tener al menos 6 caracteres"
            false
        } else {
            true
        }
    }

    private fun validateRepeatPassword(): Boolean {
        val password = textpass.text.toString().trim()
        val repeatPassword = textRepeatPassword.text.toString().trim()
        return if (TextUtils.isEmpty(repeatPassword)) {
            textRepeatPassword.error = "Repetir password es requerido"
            false
        } else if (password != repeatPassword) {
            textRepeatPassword.error = "Las contraseñas no coinciden"
            false
        } else {
            true
        }
    }
}
