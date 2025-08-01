// File: /uts/LoginActivity.kt
package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

class LoginActivity : AppCompatActivity() {
    private var rawNonce: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogle = findViewById<Button>(R.id.btnsup)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    SupabaseClientProvider.client.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    val session = SupabaseClientProvider.client.auth.currentSessionOrNull()
                    if (session != null) {
                        val repository = UserPreferencesRepository(applicationContext.dataStore)
                        repository.saveLoginData(
                            idToken = session.accessToken,
                            userId = session.user?.id ?: "",
                            refreshToken = session.refreshToken ?: "",
                            userName = session.user?.userMetadata?.get("full_name")?.toString() ?: session.user?.email ?: "",
                            userEmail = session.user?.email ?: "",
                            userAvatar = session.user?.userMetadata?.get("avatar_url")?.toString() ?: ""
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, "Login failed: No session found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("LoginActivity", "Error during email/password login: ${e.message}", e)
                    }
                }
            }
        }

        btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithGoogle() {
        rawNonce = UUID.randomUUID().toString()
        val hashedNonce = hashNonce(rawNonce)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("550699213716-fppik81ilut31ouq42kppo1ujblpg9td.apps.googleusercontent.com")
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(this)

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                handleSignInResult(result.credential)
            } catch (e: Exception) {
                handleSignInError(e)
            }
        }
    }

    private fun handleSignInResult(credential: Credential) {
        try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            lifecycleScope.launch {
                try {
                    val result = SupabaseClientProvider.client.auth.signInWith(
                        IDToken
                    ) {
                        idToken = googleIdTokenCredential.idToken
                        provider = Google
                        nonce = rawNonce
                    }

                    Log.d("AuthFlow", "Supabase login result: $result")

                    val session = SupabaseClientProvider.client.auth.currentSessionOrNull() ?: throw Exception("Session not Available")

                    Log.d("AuthFlow", "Token expires at: ${session.expiresAt}")

                    val repository = UserPreferencesRepository(applicationContext.dataStore)
                    repository.saveLoginData(
                        idToken = session.accessToken,
                        userId = session.user?.id ?: "",
                        refreshToken = session.refreshToken ?: "",
                        userName = session.user?.userMetadata?.get("full_name")?.toString() ?: session.user?.email ?: "",
                        userEmail = session.user?.email ?: "",
                        userAvatar = session.user?.userMetadata?.get("avatar_url")?.toString() ?: "",
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Supabase login failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("SupabaseLogin", "Error details:", e)
                    }
                }
            }
        } catch (e: Exception) {
            handleSignInError(e)
        }
    }

    private fun handleSignInError(exception: Exception) {
        Log.e("GoogleSignIn", "Error: ${exception.message}", exception)
        runOnUiThread {
            when (exception) {
                is GetCredentialException -> {
                    if (exception.message?.contains("user membatalkan login") == true) {
                        Toast.makeText(this@LoginActivity, "Login dibatalkan", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("GoogleSignIn", "Login failed", exception)
                        Log.e("GoogleSignIn", "Exception type: ${exception.javaClass.simpleName}")
                        Log.e("GoogleSignIn", "Exception message: ${exception.message}")
                        Log.e("GoogleSignIn", "Exception cause: ${exception.cause}")
                        Toast.makeText(this@LoginActivity, "Login gagal: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    Log.e("GoogleSignIn", "Login failed", exception)
                    Log.e("GoogleSignIn", "Exception type: ${exception.javaClass.simpleName}")
                    Log.e("GoogleSignIn", "Exception message: ${exception.message}")
                    Log.e("GoogleSignIn", "Exception cause: ${exception.cause}")
                    Toast.makeText(this@LoginActivity, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hashNonce(nonce: String): String {
        val bytes = nonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}