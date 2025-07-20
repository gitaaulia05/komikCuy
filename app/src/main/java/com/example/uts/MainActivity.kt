package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.R
import com.example.uts.PopularAdapter
import com.example.uts.PopularKomik
import com.example.uts.RecentAdapter
import com.example.uts.RecentKomik

class MainActivity : AppCompatActivity() {

    private lateinit var recentRecycler: RecyclerView
    private lateinit var popularRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Binding RecyclerView
        recentRecycler = findViewById(R.id.recentRecycler)
        popularRecycler = findViewById(R.id.popularRecycler)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val savedUsername = sharedPref.getString("username", "")

        // Layout Manager
        recentRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        popularRecycler.layoutManager = GridLayoutManager(this, 4)

        // Data List
        val recentList = listOf(
            RecentKomik(R.drawable.komika,"Komik A", "Action, Comedy", "Kisah penuh aksi mendebarkan yang dipadukan dengan humor konyol dan karakter unik. Setiap pertarungan dipenuhi kejutan, diiringi momen-momen lucu yang bikin ngakak di tengah ketegangan. Cocok buat kamu yang suka adrenaline rush tapi tetap pengen ketawa!."),
            RecentKomik(R.drawable.komikb,"Komik B", "Romance, Drama", "Sebuah kisah cinta yang penuh lika-liku, diwarnai drama kehidupan yang menyentuh hati. Setiap pertemuan, perpisahan, dan pengorbanan akan mengaduk-aduk perasaan, membuat pembaca terbawa dalam perjalanan emosi dua insan yang dipertemukan takdir."),
            RecentKomik(R.drawable.komikc,"Komik C", "Fantasy", "Jelajahi dunia penuh keajaiban, di mana sihir, makhluk legendaris, dan misteri kuno menanti di setiap sudut. Sebuah kisah petualangan epik yang membawa pembaca ke alam lain di luar batas imajinasi.")
        )

        val popularList = listOf(
            PopularKomik(R.drawable.komikx,"Komik X", "Adventure, Sci-Fi", "Petualangan seru melintasi galaksi, menghadapi teknologi canggih, alien misterius, dan planet-planet asing. Kisah ini membawa pembaca ke dunia masa depan di mana keberanian dan kecerdasan jadi senjata utama untuk bertahan hidup."),
            PopularKomik(R.drawable.komiky,"Komik Y", "Thriller, Horror", "Sebuah kisah mencekam yang dipenuhi teror tak kasat mata, misteri gelap, dan ketegangan di setiap langkah. Setiap bab akan membawa pembaca masuk ke dunia penuh rahasia kelam dan kejutan tak terduga yang bikin jantung berdebar."),
            PopularKomik(R.drawable.komikz,"Komik Z", "Fantasy, Romance", "Di dunia penuh keajaiban, dua hati dari latar berbeda dipertemukan oleh takdir. Antara sihir, makhluk legendaris, dan rahasia kuno, kisah cinta ini tumbuh di tengah petualangan yang penuh tantangan dan keindahan yang tak biasa."),
            PopularKomik(R.drawable.komikm,"Komik M", "Fantasy, Comedy", "Petualangan di dunia fantasi yang penuh keajaiban, makhluk aneh, dan sihir tak terduga, dibalut dengan humor kocak serta karakter unik yang sering terjebak dalam situasi absurd. Kisah ini siap bikin ngakak sekaligus terpesona oleh dunia ajaibnya."),
            PopularKomik(R.drawable.komikn,"Komik N", "Fantasy, Action", "Sebuah kisah petualangan epik yang menggabungkan kekuatan sihir, makhluk mistis, dan pertarungan luar biasa. Di dunia penuh keajaiban, para pahlawan menghadapi tantangan besar dengan keberanian dan kekuatan luar biasa untuk mengalahkan musuh yang mengancam dunia mereka."),
            PopularKomik(R.drawable.komico,"Komik O", "Drama, Action", "Kisah penuh emosi yang dipenuhi dengan aksi mendebarkan. Setiap keputusan dan pertempuran membawa dampak besar, menguji batasan fisik dan mental para karakternya. Di tengah pertarungan sengit, muncul ketegangan emosional yang mengubah segalanya."),
            PopularKomik(R.drawable.komikp,"Komik P", "Romance, Action", "Dua hati yang saling mencintai di tengah pertempuran dan bahaya yang mengancam. Cinta mereka diuji oleh aksi berbahaya dan keputusan-keputusan sulit, di mana setiap detik penuh dengan ketegangan, pengorbanan, dan keinginan untuk melindungi satu sama lain."),
            PopularKomik(R.drawable.komikq,"Komik Q", "Thriller, Action", "Sebuah kisah mencekam penuh aksi dan ketegangan, di mana para protagonis harus bertarung melawan waktu dan musuh yang selalu satu langkah lebih maju. Setiap detik dipenuhi bahaya, kejutan, dan keputusan sulit yang mengancam nyawa, menciptakan sebuah perjalanan yang penuh adrenaline.")
        )

        // Set Adapter
        recentRecycler.adapter = RecentAdapter(recentList)
        popularRecycler.adapter = PopularAdapter(popularList)

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

            Toast.makeText(this, "Sampai Jumpa $savedUsername !", Toast.LENGTH_SHORT).show()
            Log.d("LOGOUT", "Logout $savedUsername")
        }
    }
}
