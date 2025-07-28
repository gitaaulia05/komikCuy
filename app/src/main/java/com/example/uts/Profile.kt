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
import com.example.uts.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
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

            lifecycleScope.launch {
                try {
                    // Logout
                    SupabaseClientProvider.client.auth.signOut()
                    val repository = UserPreferencesRepository(applicationContext.dataStore)
                    repository.clearLoginData()
                    Log.e("AuthFlow", "Masuk")

                    //  Redirect ke LoginActivity
                    runOnUiThread {
                        startActivity(Intent(this@Profile, LoginActivity::class.java))
                        finish()
                    }

                } catch (e: Exception) {
                    Log.e("AuthFlow", "Logout error: ${e.message}")
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        buttonNavigation.setup(bottomNavigationView, this, R.id.menu_profile)
    }

}