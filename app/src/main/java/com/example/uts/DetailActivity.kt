package com.example.uts

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.input.key.Key.Companion.Bookmark
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts.Adapter.ChapterAdapter
import com.example.uts.LoginActivity
import com.example.uts.Model.Bookmark
import com.example.uts.Model.Chapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.MDC.put
import io.github.jan.supabase.postgrest.*

class DetailActivity : AppCompatActivity() {
    private lateinit var btnDapus : Button
    private var idKomik: Int = -1
    private lateinit var chapterRecycler: RecyclerView
    private lateinit var chapterAdapter: ChapterAdapter
    private val chapterList = mutableListOf<Chapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_detail)

        val imgResource = intent.getIntExtra("coverImageResId", 0)
        val imageUrl = intent.getStringExtra("coverImageUrl") ?: intent.getStringExtra("coverImage")
        val name = intent.getStringExtra("name")
        val idKomik = intent.getIntExtra("id_komik", -1)
        val genre = intent.getStringExtra("genre")
        val desc = intent.getStringExtra("desc")
        btnDapus = findViewById(R.id.btnDapus)

        val chapterRecycler: RecyclerView = findViewById(R.id.chapterRecycler)
        chapterRecycler.layoutManager = LinearLayoutManager(this)

        chapterAdapter = ChapterAdapter(chapterList)
        chapterRecycler.adapter = chapterAdapter

        val cover: ImageView = findViewById(R.id.coverImage)
        val txtName: TextView = findViewById(R.id.txtDetailName)
        val txtGenre: TextView = findViewById(R.id.txtDetailGenres)
        val txtDesc: TextView = findViewById(R.id.txtDetailDesc)
        val txtId : TextView = findViewById(R.id.idKomik)

        if (imgResource != 0) {
            Glide.with(this)
                .load(imgResource)
                .into(cover)
        } else if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(cover)
        } else {
            cover.setImageResource(R.drawable.aww)
        }

        txtName.text = name
        txtGenre.text = genre ?: "N/A"
        txtDesc.text = desc ?: "No description available."

        if (idKomik != -1) {
            fetchChapters(idKomik)
        } else {
            Toast.makeText(this, "Error: Comic ID not found.", Toast.LENGTH_SHORT).show()
        }

    lifecycleScope.launch {
            try {
                val repository = UserPreferencesRepository(applicationContext.dataStore)
                val userId = repository.getUserId()
                val response =
                    SupabaseClientProvider.client.from("bookmark")
                        .select(){
                            filter{
                                eq("id_komik", idKomik)
                                eq("id_user" , userId!!)
                            }
                        }
                val gson = Gson()
                val listType = object : TypeToken<List<Bookmark>>() {}.type
                val result: List<Bookmark> = gson.fromJson(response.data.toString(), listType)
                Log.d("hasil", "hasil ${result}")

                if(result.isNotEmpty()){
                    btnDapus.text = "Hapus Dari Daftar Pustaka"
                    btnDapus.setOnClickListener {
                        hapusBukuPustaka(idKomik)
                    }
                } else {
                    btnDapus.setOnClickListener {
                        tambahBukuPustaka(idKomik)
                    }
                }
            } catch (e: Exception){
                Log.e("Gagal", "gagal Ambil buku disimpan: ${e.message}")
            }
        }


        cover.setImageResource(imgResource)
        txtName.text = name
        txtId.text = idKomik.toString()

    }

    private fun tambahBukuPustaka(idKomik: Int){
        lifecycleScope.launch {

            try {
                SupabaseClientProvider.client
                    .from("bookmark")
                    .insert(mapOf("id_komik" to idKomik))

                runOnUiThread {
                    Toast.makeText(
                        this@DetailActivity,
                        "Buku Berhasil Dimasukkan ke daftar Pustaka!",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnDapus.text = "Hapus Dari Daftar Pustaka"
                    btnDapus.setOnClickListener {
                        hapusBukuPustaka(idKomik)
                    }
                }
            }catch (e: Exception) {
                Log.e("Gagal", "buku gagal: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@DetailActivity,
                        "Buku Gagal Dimasukkan ke daftar Pustaka! ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun hapusBukuPustaka(idKomik: Int){
        lifecycleScope.launch {
            try {
                val repository = UserPreferencesRepository(applicationContext.dataStore)
                val userId = repository.getUserId()
                SupabaseClientProvider.client.from("bookmark").delete {
                    filter {
                        eq("id_komik", idKomik)
                        eq("id_user" , userId!!)
                    }
                }
                runOnUiThread {
                    Toast.makeText(
                        this@DetailActivity,
                        "Buku Berhasil Dihapus dari daftar Pustaka!",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnDapus.text = "Masukkan Kedalam Daftar Pustaka"
                    btnDapus.setOnClickListener {
                        tambahBukuPustaka(idKomik)
                    }
                }
            }catch (e: Exception) {
                Log.e("Gagal", "buku gagal dihapus: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@DetailActivity,
                        "Gagal Hapus Buku Tersimpan! ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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