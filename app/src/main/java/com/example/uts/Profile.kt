package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.jan.supabase.gotrue.auth

class Profile : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var btnAdminDashboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val username: TextView = findViewById(R.id.username)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        lifecycleScope.launch {
            val repository = UserPreferencesRepository(applicationContext.dataStore)
            val userName = repository.getUserName()
            Log.d("Profile", "User Name: $userName")

            runOnUiThread {
                username.text = userName ?: "Guest"
            }
        }

        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    SupabaseClientProvider.client.auth.signOut()
                    val repository = UserPreferencesRepository(applicationContext.dataStore)
                    repository.clearLoginData()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Profile, "Sampai Jumpa!", Toast.LENGTH_SHORT).show()
                        Log.d("Profile", "Logout successful")
                        val intent = Intent(this@Profile, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Profile, "Gagal logout: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("Profile", "Error during logout", e)
                    }
                }
            }
        }

        btnAdminDashboard = findViewById(R.id.btnAdminDashboard)
        btnAdminDashboard.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        buttonNavigation.setup(bottomNavigationView, this, R.id.menu_profile)
    }
}