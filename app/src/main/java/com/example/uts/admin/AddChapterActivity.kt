package com.example.uts

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.uts.Model.Chapter
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.lang.reflect.Array.set

class AddChapterActivity : AppCompatActivity() {

    private lateinit var tvChapterTitle: TextView
    private lateinit var etChapterTitle: EditText
    private lateinit var btnSaveChapter: Button

    private var comicId: Int = 1 // ID komik yang akan ditambahkan chapternya

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chapter)

        tvChapterTitle = findViewById(R.id.tvAddChapterTitle)
        etChapterTitle = findViewById(R.id.etChapterTitle)
        btnSaveChapter = findViewById(R.id.btnSaveChapter)

        // Mengambil comicId dari intent
        comicId = intent.getIntExtra("comic_id", -1)
        val comicTitle = intent.getStringExtra("comic_title")

        if (comicId == -1 || comicTitle == null) {
            Toast.makeText(this, "Error: Comic ID not provided.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvChapterTitle.text = "Add Chapter for $comicTitle"

        btnSaveChapter.setOnClickListener {
            saveChapter()
        }
    }

    private fun saveChapter() {
        val chapterTitle = etChapterTitle.text.toString().trim()
        if (chapterTitle.isEmpty()) {
            Toast.makeText(this, "Judul chapter tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                val newChapter = Chapter(
                    id_komik = comicId,
                    nama_chapter = chapterTitle,
                    created_at = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
                )
                // Menggunakan data class untuk insert
                SupabaseClientProvider.client.postgrest["chapter"].insert(newChapter)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddChapterActivity, "Chapter added successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Log.e("AddChapterActivity", "Error adding chapter: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddChapterActivity, "Failed to add chapter: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
