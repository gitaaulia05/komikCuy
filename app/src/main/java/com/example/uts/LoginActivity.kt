package com.example.uts

import android.R.attr.onClick
import android.content.Intent
import androidx.credentials.CredentialManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.Credential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.*
import java.security.MessageDigest
import java.util.UUID


class LoginActivity : AppCompatActivity() {
    private val RC_GOOGLE_SIGN_IN = 9001
    private var rawNonce: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val Username = findViewById<EditText>(R.id.etUsername)
        val Password = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogle = findViewById<Button>(R.id.btnsup)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val inputUsername = Username.text.toString()
            val inputPassword = Password.text.toString()

            // Ambil data dari SharedPreferences
            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
            val savedUsername = sharedPref.getString("username", "")
            val savedPassword = sharedPref.getString("password", "")

            if (inputUsername == savedUsername && inputPassword == savedPassword) {
                Toast.makeText(this, "Selamat Datang! $inputUsername", Toast.LENGTH_SHORT).show()
                Log.d("LOGIN", "Login sukses untuk user $inputUsername")

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show()
                Log.d("LOGIN", "Login gagal untuk user $inputUsername")
            }
        }


        val btnGoogleSignIn = btnGoogle
        btnGoogleSignIn.setOnClickListener {
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
            .setServerClientId("550699213716-ln5hffjr0utg5a9evcm0jt939qpbefrp.apps.googleusercontent.com")
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
                        idToken = session.accessToken ?: googleIdTokenCredential.idToken ,
                        userId = session.user?.id ?: "",
                        refreshToken = session.refreshToken ?: "",
                        userName = session.user?.userMetadata?.get("full_name")?.toString() ?: "",
                        userEmail = session.user?.email ?: "" ,
                        userAvatar = session.user?.userMetadata?.get("avatar_url")?.toString() ?: "",
                    )
                    Log.d("AuthFlow", "Data saved to DataStore")
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Sign in successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }


                } catch (e: Exception) {
                    runOnUiThread {
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
                    if (exception.message?.contains("The user canceled the operation") == true) {
                        Toast.makeText(this@LoginActivity, "Sign in cancelled", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Sign in failed: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
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
