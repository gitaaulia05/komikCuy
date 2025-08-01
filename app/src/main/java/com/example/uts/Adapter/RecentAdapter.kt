package com.example.uts.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts.DetailActivity
import com.example.uts.Model.Chapter
import com.example.uts.R
import com.example.uts.Model.RecentKomik
import com.example.uts.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class RecentAdapter(private val recentList: List<RecentKomik>) :
    RecyclerView.Adapter<RecentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        val txtName: TextView = view.findViewById(R.id.Nama)
        val tvComicGenre: TextView = view.findViewById(R.id.tvComicGenre)
        val tvChapterCount: TextView = view.findViewById(R.id.tvChapterCount)
    }

    suspend fun getChapterCountForComic(comicId: Int): Int {
        return try {
            val chapters = SupabaseClientProvider.client
                .postgrest["chapter"]
                .select {
                    filter {
                        eq("id_komik", comicId)
                    }
                }
                .decodeList<Chapter>()
            chapters.size
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = recentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comic = recentList[position]
        holder.txtName.text = comic.judul_komik
        holder.tvComicGenre.text = comic.genre

        // Load image from URL using Glide
                Glide.with(holder.itemView.context)
                    .load(comic.gambar_komik)
                    .into(holder.coverImage)

        if (holder.itemView.context is AppCompatActivity) {
            (holder.itemView.context as AppCompatActivity).lifecycleScope.launch {
                val chapterCount = getChapterCountForComic(comic.id_komik)
                holder.tvChapterCount.text = "$chapterCount Chapter"
            }
        } else {
            holder.tvChapterCount.text = "N/A Chapters"
            Log.e("AdminComicAdapter", "Context is not AppCompatActivity, cannot use lifecycleScope")
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("coverImage", comic.gambar_komik)
                putExtra("name", comic.judul_komik)
                putExtra("id_komik", comic.id_komik)
                putExtra("genre", comic.genre)
                putExtra("desc", comic.desc)

            }
            context.startActivity(intent)
        }
    }
}

