package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.Adapter.AdminComicAdapter
import com.example.uts.Model.RecentKomik
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlin.jvm.java

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var adminComicRecycler: RecyclerView
    private lateinit var btnAddComic: Button
    private lateinit var btnCustomerService: Button
    private lateinit var adminComicAdapter: AdminComicAdapter
    private val komikList = mutableListOf<RecentKomik>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        adminComicRecycler = findViewById(R.id.adminComicRecycler)
        btnAddComic = findViewById(R.id.btnAddComic)

        adminComicRecycler.layoutManager = LinearLayoutManager(this)
        adminComicAdapter = AdminComicAdapter(
            komikList, this,
            onComicDeleted = {
                fetchComics()
            },
            onComicEdited = { comic ->
                val intent = Intent(this, AddEditComicActivity::class.java).apply {
                    putExtra("comic_id", comic.id_komik)
                    putExtra("comic_title", comic.judul_komik)
                    putExtra("comic_image_url", comic.gambar_komik)
                    putExtra("comic_genre", comic.genre)
                    putExtra("comic_desc", comic.desc)
                }
                startActivity(intent)
            },
            onAddChapterClicked = { comic ->
                val intent = Intent(this, AddChapterActivity::class.java).apply {
                    putExtra("comic_id", comic.id_komik)
                    putExtra("comic_title", comic.judul_komik)
                }
                startActivity(intent)
            }
        )
        adminComicRecycler.adapter = adminComicAdapter

        btnAddComic.setOnClickListener {
            val intent = Intent(this, AddEditComicActivity::class.java)
            startActivity(intent)
        }
        btnCustomerService = findViewById(R.id.btnCustomerService)

        btnCustomerService.setOnClickListener {
            val intent = Intent(this, AdminPengajuanActivity::class.java)
            startActivity(intent)
        }
        fetchComics()
    }

    override fun onResume() {
        super.onResume()
        fetchComics()
    }

    private fun fetchComics() {
        lifecycleScope.launch {
            try {
                val response = SupabaseClientProvider.client.postgrest["komik"].select()
                val gson = Gson()
                val listType = object : TypeToken<List<RecentKomik>>() {}.type
                val result: List<RecentKomik> = gson.fromJson(response.data.toString(), listType)

                komikList.clear()
                komikList.addAll(result)
                adminComicAdapter.notifyDataSetChanged()
                Log.d("AdminDashboard", "Comics fetched: ${komikList.size}")
            } catch (e: Exception) {
                Toast.makeText(this@AdminDashboardActivity, "Failed to load comics: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("AdminDashboard", "Error fetching comics", e)
            }
        }
    }
}