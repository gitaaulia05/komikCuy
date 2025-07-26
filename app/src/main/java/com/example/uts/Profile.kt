package com.example.uts

import com.example.uts.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Profile : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

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