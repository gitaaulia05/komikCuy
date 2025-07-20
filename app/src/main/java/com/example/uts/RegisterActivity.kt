package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val backLogin = findViewById<Button>(R.id.backToLogin)

        buttonRegister.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
            val editor = sharedPref.edit()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password.length < 8) {
                Toast.makeText(this, "Password harus memiliki minimal 8 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            editor.putString("username", username)
            editor.putString("password", password)
            editor.apply()

            Log.d("RegisterActivity", "Username: $username, Password: $password") // Log
            Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show() // Toast

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        backLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}