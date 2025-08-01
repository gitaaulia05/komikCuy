package com.example.uts

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CustomerSupport : AppCompatActivity() {

    private val supabaseUrl = "https://gjiahecfesmuttvcpuiv.supabase.co/rest/v1/pengajuan_cs"
    private val supabaseApiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdqaWFoZWNmZXNtdXR0dmNwdWl2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTI1MzY2NzYsImV4cCI6MjA2ODExMjY3Nn0.kSe95OtHvOaAIQru6eG2uosD0lEadUAtATLFGBLJebM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_support)

        val etJudul = findViewById<EditText>(R.id.etJudul)
        val etDeskripsi = findViewById<EditText>(R.id.etDeskripsi)
        val spinnerJenis = findViewById<Spinner>(R.id.spinnerJenis)
        val btnKirim = findViewById<Button>(R.id.btnKirim)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // ISI spinner dengan jenis masalah
        val jenisOptions = listOf("Bug", "Error", "Masukan", "Lainnya")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenis.adapter = spinnerAdapter

        btnKirim.setOnClickListener {
            val judul = etJudul.text.toString().trim()
            val deskripsi = etDeskripsi.text.toString().trim()
            val jenis = spinnerJenis.selectedItem.toString()
            val status = "Diterima"
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val tanggal = dateFormat.format(Date())

            if (judul.isEmpty() || deskripsi.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val json = """
                {
                    "judul_pengajuan": "$judul",
                    "deskripsi_masalah": "$deskripsi",
                    "jenis_masalah": "$jenis",
                    "status": "$status",
                    "tanggal": "$tanggal"
                }
            """.trimIndent()

            sendPengajuan(json)
        }

        // Bottom nav (jika ada class helper)
        buttonNavigation.setup(bottomNavigationView, this, R.id.menu_cs)
    }

    private fun sendPengajuan(jsonBody: String) {
        val client = OkHttpClient()

        val body = jsonBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(supabaseUrl)
            .addHeader("apikey", supabaseApiKey)
            .addHeader("Authorization", "Bearer $supabaseApiKey")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CustomerSupport, "Gagal mengirim pengajuan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CustomerSupport, "Pengajuan berhasil dikirim", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@CustomerSupport, "Gagal: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        buttonNavigation.setup(bottomNav, this, R.id.menu_cs)
    }
}
