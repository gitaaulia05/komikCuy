package com.example.uts

import com.example.uts.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch


class Profile : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val username : TextView = findViewById(R.id.username)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        lifecycleScope.launch {
            val repository = UserPreferencesRepository(applicationContext.dataStore)

            val userName = repository.getUserName()
            Log.d("MainActivity", "User Name: $userName")

            // Set ke TextView
            runOnUiThread {
                username.text = userName ?: "Guest"
            }
        }
        // Logout event klik
        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

            Toast.makeText(this, "Sampai Jumpa ", Toast.LENGTH_SHORT).show()
            Log.d("LOGOUT", "Logout ")
        }

    }

    override fun onResume() {
        super.onResume()
        buttonNavigation.setup(bottomNavigationView, this, R.id.menu_profile)
    }

}