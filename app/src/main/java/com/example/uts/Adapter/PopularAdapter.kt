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
import com.example.uts.Model.PopularKomik
import com.example.uts.Model.RecentKomik
import com.example.uts.R
import com.example.uts.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class PopularAdapter(private val listKomik: List<PopularKomik>) :
    RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    class PopularViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        val genre: TextView = view.findViewById(R.id.genre)
        val chapter: TextView = view.findViewById(R.id.tvChapterCount)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular, parent, false)
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val komik = listKomik[position]

        Glide.with(holder.itemView.context)
            .load(komik.image)
            .into(holder.image)

        holder.name.text = komik.name
        holder.genre.text = komik.genre

        if (holder.itemView.context is AppCompatActivity) {
            (holder.itemView.context as AppCompatActivity).lifecycleScope.launch {
                val chapterCount = getChapterCountForComic(komik.id_komik)
                holder.chapter.text = "$chapterCount Chapter"
            }
        } else {
            holder.chapter.text = "N/A Chapters"
            Log.e("AdminComicAdapter", "Context is not AppCompatActivity, cannot use lifecycleScope")
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("id_komik", komik.id_komik)
                putExtra("coverImage", komik.image)
                putExtra("name", komik.name)
                putExtra("genre", komik.genre)
                putExtra("desc", komik.desc)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listKomik.size
}