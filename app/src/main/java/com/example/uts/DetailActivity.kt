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
import com.example.uts.ChapterData
import com.example.uts.LoginActivity
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_detail)

        btnDapus = findViewById(R.id.btnDapus)

        val imgResource = intent.getIntExtra("coverImage", 0)
        val name = intent.getStringExtra("name")
        idKomik = intent.getIntExtra("id_komiks", -1)

        val chapterRecycler: RecyclerView = findViewById(R.id.chapterRecycler)
        chapterRecycler.layoutManager = LinearLayoutManager(this)



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
                        // Button Dapus
                        btnDapus.setOnClickListener {
                            tambahBukuPustaka(idKomik)
                        }
                    }
                } catch (e: Exception){
                    Log.e("Gagal", "gagal Ambil buku disimpan: ${e.message}")
                }
        }

        val chapterList = listOf(
            ChapterData("Chapter 1: Awal Petualangan"),
            ChapterData("Chapter 2: Rahasia Terungkap"),
            ChapterData("Chapter 3: Lah Terungkap"),
            ChapterData("Chapter 4: Astaga Terungkap"),
            ChapterData("Chapter 5: Loh Terungkap"),
            ChapterData("Final Chapter 6: Pertempuran Besar")
        )

        val chapterAdapter = ChapterAdapter(chapterList)
        chapterRecycler.adapter = chapterAdapter


        val cover: ImageView = findViewById(R.id.coverImage)
        val txtName: TextView = findViewById(R.id.txtDetailName)
        val txtGenres: TextView = findViewById(R.id.txtDetailGenres)
        val txtDesc: TextView = findViewById(R.id.txtDetailDesc)
        val txtId : TextView = findViewById(R.id.idKomik)

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

}
