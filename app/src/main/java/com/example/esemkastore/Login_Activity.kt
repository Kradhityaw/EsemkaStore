package com.example.esemkastore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.esemkastore.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Login_Activity : AppCompatActivity() {
    lateinit var bind : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.loginEmail.setText("khuddle0@cbc.ca")
        bind.loginPassword.setText("P@ssw0rd123")

        bind.loginButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val conn = URL("http://10.0.2.2:5000/api/Login").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")

                val jsons = JSONObject().apply {
                    put("email", bind.loginEmail.text)
                    put("password", bind.loginPassword.text)
                }

                conn.outputStream.write(jsons.toString().toByteArray())

                if (conn.responseCode in 200..299) {
                    var input = conn.inputStream.bufferedReader().readText()
                    Runtime.userID = JSONObject(input).getString("id")
                    startActivity(Intent(this@Login_Activity, MainActivity::class.java))
                    finish()
                }
                else {
                    runOnUiThread {
                        Toast.makeText(this@Login_Activity, "${JSONObject(conn.errorStream.bufferedReader().readText()).getString("title")}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}