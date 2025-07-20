package com.example.uts

import android.content.Intent
import androidx.credentials.CredentialManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.*
import java.security.MessageDigest
import java.util.UUID

class LoginActivity : AppCompatActivity() {
    private val RC_GOOGLE_SIGN_IN = 9001

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
        val rawNonce = UUID.randomUUID().toString()
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
            val idToken = googleIdTokenCredential.idToken

            Log.d("GoogleSignIn", "ID Token: $idToken")
            Toast.makeText(this, "Sign in successful!", Toast.LENGTH_SHORT).show()

            // Navigasi ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } catch (e: Exception) {
            handleSignInError(e)
        }
    }
    private fun handleSignInError(exception: Exception) {
        Log.e("GoogleSignIn", "Error: ${exception.message}", exception)
        when (exception) {
            is GetCredentialException -> {
                if (exception.message?.contains("The user canceled the operation") == true) {
                    Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Sign in failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
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