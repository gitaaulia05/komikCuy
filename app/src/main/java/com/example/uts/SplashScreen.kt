package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch
import kotlin.jvm.java
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splash_screen)

        lifecycleScope.launch {
            val repository = UserPreferencesRepository(applicationContext.dataStore)
            Log.d("SplashFlow", "Checking login state...")

            try {
                val isLoggedIn = repository.isUserLoggedIn()
                Log.d("SplashFlow", "isUserLoggedIn: $isLoggedIn")
                val session = SupabaseClientProvider.client.auth.currentSessionOrNull()

                if(isLoggedIn){
                    // Cek session Supabase
                    kotlinx.coroutines.delay(1000)
                    val session = SupabaseClientProvider.client.auth.currentSessionOrNull()
                    Log.d("SplashFlow", "Session: ${session != null}")

                    if (session != null) {
                        // Cek expired token
                        val now = Clock.System.now()
                        val expiresAt = session.expiresAt

                        Log.d("SplashFlow", "Token expires at: $expiresAt")
                        Log.d("SplashFlow", "Current time: $now")

                        if (now < expiresAt) {
                            Log.d("SplashFlow", "Session valid, going to MainActivity")
                            goToMainActivity()
                            return@launch
                        } else {
                            Log.d("SplashFlow", "Session expired")
                            repository.clearLoginState()
                        }
                    } else {
                        Log.d("SplashFlow", "No session found, clearing login state")
                        repository.clearLoginState()
                    }
                }
                goToLoginActivity()
            } catch (e: Exception) {
                Log.e("SplashActivity", "Error checking auth state", e)
                goToLoginActivity()
            }
        }
    }

    private fun goToLoginActivity() {
        Intent(this, LoginActivity::class.java).also{
            startActivity(it)
            finish();
        }
    }

    private fun goToMainActivity() {
        Intent(this, MainActivity::class.java).also{
            startActivity(it)
            finish();
        }
    }
}