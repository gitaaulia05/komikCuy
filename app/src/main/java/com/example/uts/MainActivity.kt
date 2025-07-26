package com.example.uts


import android.app.Activity
import com.example.uts.R

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import androidx.annotation.NonNull;
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private lateinit var recentRecycler: RecyclerView
    private lateinit var popularRecycler: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private val komikList = mutableListOf<RecentKomik>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // Binding RecyclerView
        recentRecycler = findViewById(R.id.recentRecycler)
        popularRecycler = findViewById(R.id.popularRecycler)

        val recentAdapter = RecentAdapter(komikList)
        recentRecycler.adapter = recentAdapter

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val savedUsername = sharedPref.getString("username", "")

        // Layout Manager
        recentRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        popularRecycler.layoutManager = GridLayoutManager(this, 4)

        // Data List
        val popularList = listOf(
            PopularKomik(
                R.drawable.komikx,
                "Komik X",
                "Adventure, Sci-Fi",
                "Petualangan seru melintasi galaksi, menghadapi teknologi canggih, alien misterius, dan planet-planet asing. Kisah ini membawa pembaca ke dunia masa depan di mana keberanian dan kecerdasan jadi senjata utama untuk bertahan hidup."
            ),
            PopularKomik(
                R.drawable.komiky,
                "Komik Y",
                "Thriller, Horror",
                "Sebuah kisah mencekam yang dipenuhi teror tak kasat mata, misteri gelap, dan ketegangan di setiap langkah. Setiap bab akan membawa pembaca masuk ke dunia penuh rahasia kelam dan kejutan tak terduga yang bikin jantung berdebar."
            ),
            PopularKomik(
                R.drawable.komikz,
                "Komik Z",
                "Fantasy, Romance",
                "Di dunia penuh keajaiban, dua hati dari latar berbeda dipertemukan oleh takdir. Antara sihir, makhluk legendaris, dan rahasia kuno, kisah cinta ini tumbuh di tengah petualangan yang penuh tantangan dan keindahan yang tak biasa."
            ),
            PopularKomik(
                R.drawable.komikm,
                "Komik M",
                "Fantasy, Comedy",
                "Petualangan di dunia fantasi yang penuh keajaiban, makhluk aneh, dan sihir tak terduga, dibalut dengan humor kocak serta karakter unik yang sering terjebak dalam situasi absurd. Kisah ini siap bikin ngakak sekaligus terpesona oleh dunia ajaibnya."
            ),
            PopularKomik(
                R.drawable.komikn,
                "Komik N",
                "Fantasy, Action",
                "Sebuah kisah petualangan epik yang menggabungkan kekuatan sihir, makhluk mistis, dan pertarungan luar biasa. Di dunia penuh keajaiban, para pahlawan menghadapi tantangan besar dengan keberanian dan kekuatan luar biasa untuk mengalahkan musuh yang mengancam dunia mereka."
            ),
            PopularKomik(
                R.drawable.komico,
                "Komik O",
                "Drama, Action",
                "Kisah penuh emosi yang dipenuhi dengan aksi mendebarkan. Setiap keputusan dan pertempuran membawa dampak besar, menguji batasan fisik dan mental para karakternya. Di tengah pertarungan sengit, muncul ketegangan emosional yang mengubah segalanya."
            ),
            PopularKomik(
                R.drawable.komikp,
                "Komik P",
                "Romance, Action",
                "Dua hati yang saling mencintai di tengah pertempuran dan bahaya yang mengancam. Cinta mereka diuji oleh aksi berbahaya dan keputusan-keputusan sulit, di mana setiap detik penuh dengan ketegangan, pengorbanan, dan keinginan untuk melindungi satu sama lain."
            ),
            PopularKomik(
                R.drawable.komikq,
                "Komik Q",
                "Thriller, Action",
                "Sebuah kisah mencekam penuh aksi dan ketegangan, di mana para protagonis harus bertarung melawan waktu dan musuh yang selalu satu langkah lebih maju. Setiap detik dipenuhi bahaya, kejutan, dan keputusan sulit yang mengancam nyawa, menciptakan sebuah perjalanan yang penuh adrenaline."
            )
        )

        // Set Adapter

       // recent komik
        lifecycleScope.launch {
            try{
                val response = SupabaseClientProvider.client.postgrest["komik"].select()
                // Decode dengan json
                val gson = Gson()
                val listType = object : TypeToken<List<RecentKomik>>() {}.type
                val result: List<RecentKomik> = gson.fromJson(response.data.toString(), listType)

                // Update RecyclerView
                komikList.clear()
                komikList.addAll(result)
                // reload adapter
                recentAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        popularRecycler.adapter = PopularAdapter(popularList)


    }

    override fun onResume() {
        super.onResume()
        buttonNavigation.setup(bottomNavigationView, this, R.id.menu_home)
    }


}
