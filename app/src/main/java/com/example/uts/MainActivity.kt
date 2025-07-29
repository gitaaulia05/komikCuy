package com.example.uts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.lifecycleScope
import com.example.uts.Adapter.PopularAdapter
import com.example.uts.Adapter.RecentAdapter
import com.example.uts.Model.PopularKomik
import com.example.uts.Model.RecentKomik
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var recentRecycler: RecyclerView
    private lateinit var popularRecycler: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private val recentKomikList = mutableListOf<RecentKomik>()
    private val popularKomikList = mutableListOf<PopularKomik>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // Binding RecyclerView
        recentRecycler = findViewById(R.id.recentRecycler)
        popularRecycler = findViewById(R.id.popularRecycler)

        // RECYLER ADAPTER NILAI DARI KOMIK LIST
        val recentAdapter = RecentAdapter(recentKomikList)
        recentRecycler.adapter = recentAdapter

        val popularAdapter = PopularAdapter(popularKomikList)
        popularRecycler.adapter = popularAdapter


        // Layout Manager
        recentRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        popularRecycler.layoutManager = GridLayoutManager(this, 4)

        // Fetch Recent Comics
        lifecycleScope.launch {
            try {
                val response = SupabaseClientProvider.client.postgrest["komik"].select()
                val gson = Gson()
                val listType = object : TypeToken<List<RecentKomik>>() {}.type
                val result: List<RecentKomik> = gson.fromJson(response.data.toString(), listType)

                recentKomikList.clear()
                recentKomikList.addAll(result)
                recentAdapter.notifyDataSetChanged()
                Log.d("MainActivity", "Recent comics fetched: ${recentKomikList.size}")

                popularKomikList.clear()
                result.forEach { recentComic ->
                    popularKomikList.add(
                        PopularKomik(
                            image = recentComic.gambar_komik.toInt(),
                            name = recentComic.judul_komik,
                            genre = recentComic.genre,
                            desc = recentComic.desc
                        )
                    )
                }
                popularAdapter.notifyDataSetChanged()
                Log.d("MainActivity", "Popular comics fetched: ${popularKomikList.size}")

            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching comics: ${e.message}", e)
                // Tampilkan Toast atau pesan error ke pengguna
            }
        }
    }

    // BUTTON NAVIGASI
    override fun onResume() {
        super.onResume()
        buttonNavigation.setup(bottomNavigationView, this, R.id.menu_home)
    }
}