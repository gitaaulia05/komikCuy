package com.example.uts

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts.Adapter.ChapterAdapter
import com.example.uts.Model.Chapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var chapterRecycler: RecyclerView
    private lateinit var chapterAdapter: ChapterAdapter
    private val chapterList = mutableListOf<Chapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_detail)

        // Ambil kedua jenis data gambar
        val imgResource = intent.getIntExtra("coverImageResId", 0) // Dari PopularAdapter
        val imageUrl = intent.getStringExtra("coverImageUrl") ?: intent.getStringExtra("coverImage") // Dari RecentAdapter atau PopularAdapter (jika ada URL)

        val name = intent.getStringExtra("name")
        val idKomik = intent.getIntExtra("id_komiks", -1)
        val genres = intent.getStringExtra("genres")
        val desc = intent.getStringExtra("desc")

        chapterRecycler = findViewById(R.id.chapterRecycler)
        chapterRecycler.layoutManager = LinearLayoutManager(this)

        chapterAdapter = ChapterAdapter(chapterList)
        chapterRecycler.adapter = chapterAdapter

        val cover: ImageView = findViewById(R.id.coverImage)
        val txtName: TextView = findViewById(R.id.txtDetailName)
        val txtGenres: TextView = findViewById(R.id.txtDetailGenres)
        val txtDesc: TextView = findViewById(R.id.txtDetailDesc)

        if (imgResource != 0) {
            // Muat dari ID sumber daya drawable
            Glide.with(this)
                .load(imgResource)
                .into(cover)
        } else if (!imageUrl.isNullOrEmpty()) {
            // Muat dari URL
            Glide.with(this)
                .load(imageUrl)
                .into(cover)
        } else {
            // Set placeholder jika tidak ada gambar
            cover.setImageResource(R.drawable.aww) // Ganti dengan placeholder Anda
        }

        txtName.text = name
        txtGenres.text = genres ?: "N/A"
        txtDesc.text = desc ?: "No description available."

        if (idKomik != -1) {
            fetchChapters(idKomik)
        } else {
            Toast.makeText(this, "Error: Comic ID not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchChapters(comicId: Int) {
        lifecycleScope.launch {
            try {
                val response = SupabaseClientProvider.client.postgrest["chapter"]
                    .select {
                        filter {
                            eq("id_komik", comicId)
                        }
                    }
                val gson = Gson()
                val listType = object : TypeToken<List<Chapter>>() {}.type
                val result: List<Chapter> = gson.fromJson(response.data.toString(), listType)

                chapterList.clear()
                chapterList.addAll(result)
                chapterAdapter.notifyDataSetChanged()
                Log.d("DetailActivity", "Chapters fetched: ${chapterList.size} for comic ID: $comicId")
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "Failed to load chapters: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("DetailActivity", "Error fetching chapters", e)
            }
        }
    }
}