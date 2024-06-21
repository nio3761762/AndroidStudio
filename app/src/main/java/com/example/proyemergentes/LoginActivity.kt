package com.example.proyemergentes

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyemergentes.Intrface.RetrofitCliente
import com.example.proyemergentes.dataclass.UserRequerido
import com.example.proyemergentes.dataclass.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    var urlex= "http://192.168.0.10:3000"
    var users= Users("","","","")
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.iniTheme)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        val btnlogin = findViewById<Button>(R.id.loginButton)
        val btnRegistrar = findViewById<Button>(R.id.signupButton)

        btnRegistrar.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        btnlogin.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if(username.isEmpty() && password.isEmpty()){

                usernameEditText.error = "Username is required"
            usernameEditText.requestFocus()
                passwordEditText.error = "Password is required"
                passwordEditText.requestFocus()
            return@setOnClickListener}
            else {
                if (username.isEmpty()) {
                    usernameEditText.error = "Username is required"
                    usernameEditText.requestFocus()
                    return@setOnClickListener
                }else
                    if(password.isEmpty())
                    {
                        passwordEditText.error = "Password is required"
                        passwordEditText.requestFocus()
                        return@setOnClickListener
                    }
//                    else
//                if (password.length < 6){
//                    passwordEditText.error = "Password debe tener al menos 6 caracteres"
//                    passwordEditText.requestFocus()
//                    return@setOnClickListener
//                }

            }

            if(!username.isEmpty() && !password.isEmpty())
                  login(username,password)

            usernameEditText.text.clear()
            passwordEditText.text.clear()
        }

    }

    fun login(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginRequest = UserRequerido(username, password)
                val call = RetrofitCliente.webService.login(loginRequest)

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        val loginResponse = call.body()
                        if (loginResponse != null) {

                                RetrofitCliente.setAuthToken(loginResponse.Token)


                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("user",loginResponse)
                            startActivity(intent)
                            finish()
                    } else {
                       }

                    } else {
                     }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
             //       Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
