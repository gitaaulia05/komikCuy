package com.example.uts.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts.DetailActivity
import com.example.uts.R
import com.example.uts.Model.RecentKomik

class RecentAdapter(private val recentList: List<RecentKomik>) :
    RecyclerView.Adapter<RecentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        val txtName: TextView = view.findViewById(R.id.Nama)
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

        // Load image from URL using Glide
                Glide.with(holder.itemView.context)
                    .load(comic.gambar_komik)
                    .into(holder.coverImage)

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

