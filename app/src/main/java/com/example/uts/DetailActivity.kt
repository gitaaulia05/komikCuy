package com.example.uts

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.ChapterData

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_detail)

        val imgResource = intent.getIntExtra("coverImage", 0)
        val name = intent.getStringExtra("name")
        val genres = intent.getStringExtra("genres")
        val desc = intent.getStringExtra("desc")

        val chapterRecycler: RecyclerView = findViewById(R.id.chapterRecycler)
        chapterRecycler.layoutManager = LinearLayoutManager(this)

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

        cover.setImageResource(imgResource)
        txtName.text = name
        txtGenres.text = genres
        txtDesc.text = desc
    }
}
