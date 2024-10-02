package com.example.appceluaws

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var tvNombre: TextView? = null
    private var tvCorreo: TextView? = null
    private var tvRut: TextView? = null
    private var btnObtenerUsuario: Button? = null
    private var btnObtenerTodos: Button? = null
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvNombre = findViewById(R.id.tv_nombre)
        tvCorreo = findViewById(R.id.tv_correo)
        tvRut = findViewById(R.id.tv_rut)
        btnObtenerUsuario = findViewById(R.id.btn_obtener_usuario)
        btnObtenerTodos = findViewById(R.id.btn_obtener_todos)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnObtenerUsuario!!.setOnClickListener { obtenerUsuario() }
        btnObtenerTodos!!.setOnClickListener { obtenerTodosLosUsuarios() }
    }

    private fun obtenerUsuario() {
        val client = OkHttpClient()
        val username = etUsername?.text.toString()
        val password = etPassword?.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            runOnUiThread {
                tvNombre?.text = "Por favor, ingrese usuario y contraseña"
            }
            return
        }

        val url = "http://3.214.248.100:8081/usuario?username=$username&password=$password"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    tvNombre?.text = "Error al obtener usuario"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val myResponse = response.body?.string()
                    try {
                        val jsonObject = JSONObject(myResponse!!)
                        runOnUiThread {
                            tvNombre?.text = "Nombre: " + jsonObject.getString("nombre")
                            tvCorreo?.text = "Correo: " + jsonObject.getString("correo")
                            tvRut?.text = "RUT: " + jsonObject.getString("rut")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            tvNombre?.text = "Error al procesar la respuesta"
                        }
                    }
                } else {
                    runOnUiThread {
                        tvNombre?.text = "Credenciales inválidas"
                    }
                }
            }
        })
    }

    private fun obtenerTodosLosUsuarios() {
        val client = OkHttpClient()
        val url = "http://3.214.248.100:8081/usuarios"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    tvNombre?.text = "Error al obtener usuarios"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val myResponse = response.body?.string()
                    try {
                        val jsonArray = JSONArray(myResponse!!)
                        var resultado = ""
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            resultado += "Nombre: " + jsonObject.getString("nombre") + "\n"
                            resultado += "Correo: " + jsonObject.getString("correo") + "\n"
                            resultado += "RUT: " + jsonObject.getString("rut") + "\n\n"
                        }
                        runOnUiThread {
                            tvNombre?.text = resultado
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            tvNombre?.text = "Error al procesar la respuesta"
                        }
                    }
                } else {
                    runOnUiThread {
                        tvNombre?.text = "Error al obtener usuarios"
                    }
                }
            }
        })
    }
}
