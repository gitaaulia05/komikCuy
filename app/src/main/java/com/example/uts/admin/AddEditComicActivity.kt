package com.example.uts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddEditComicActivity : AppCompatActivity() {

    private lateinit var tvAddEditTitle: TextView
    private lateinit var etComicTitle: EditText
    private lateinit var etComicImageUrl: EditText
    private lateinit var etComicDesc: EditText
    private lateinit var etComicGenre: EditText
    private lateinit var btnSaveComic: Button

    private var comicId: Int = -1 // -1 indicates new comic, otherwise it's an existing comic ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_comic)

        tvAddEditTitle = findViewById(R.id.tvAddEditTitle)
        etComicTitle = findViewById(R.id.etComicTitle)
        etComicDesc = findViewById(R.id.etComicDesc)
        etComicGenre = findViewById(R.id.etComicGenre)
        etComicImageUrl = findViewById(R.id.etComicImageUrl)
        btnSaveComic = findViewById(R.id.btnSaveComic)

        // Check if we are editing an existing comic
        comicId = intent.getIntExtra("comic_id", -1)
        if (comicId != -1) {
            tvAddEditTitle.text = "Edit Comic"
            etComicTitle.setText(intent.getStringExtra("comic_title"))
            etComicImageUrl.setText(intent.getStringExtra("comic_image_url"))
            etComicDesc.setText(intent.getStringExtra("comic_desc"))
            etComicGenre.setText(intent.getStringExtra("comic_genre"))
        } else {
            tvAddEditTitle.text = "Add New Comic"
        }

        btnSaveComic.setOnClickListener {
            saveComic()
        }
    }

    private fun saveComic() {
        val title = etComicTitle.text.toString().trim()
        val imageUrl = etComicImageUrl.text.toString().trim()
        val genre = etComicGenre.text.toString().trim()
        val desc = etComicDesc.text.toString().trim()

        if (title.isEmpty() || genre.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill Title, Genre, and Description fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                if (comicId == -1) {
                    val newComic = mapOf(
                        "judul_komik" to title,
                        "gambar_komik" to imageUrl,
                        "genre" to genre,
                        "desc" to desc,
                        "created_at" to Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
                    )
                    SupabaseClientProvider.client.postgrest["komik"].insert(newComic)
                    Toast.makeText(this@AddEditComicActivity, "Comic added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Update existing comic
                    SupabaseClientProvider.client.postgrest.from("komik")
                        .update(
                            {
                                set("judul_komik", title)
                                set("gambar_komik", imageUrl)
                                set("genre", genre)
                                set("desc", desc)
                            },
                            {
                                filter {
                                    eq("id_komik", comicId)
                                }
                            }
                        )
                    Toast.makeText(this@AddEditComicActivity, "Comic updated successfully!", Toast.LENGTH_SHORT).show()
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@AddEditComicActivity, "Failed to save comic: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}