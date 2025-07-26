package com.example.uts

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PopularAdapter(private val popularList: List<PopularKomik>) :
    RecyclerView.Adapter<PopularAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        val txtName: TextView = view.findViewById(R.id.Nama)
//        val txtGenres: TextView = view.findViewById(R.id.Genre)
//        val txtDesc: TextView = view.findViewById(R.id.Desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = popularList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comic = popularList[position]
        holder.coverImage.setImageResource(comic.image)
        holder.txtName.text = comic.name
//        holder.txtGenres.text = comic.genre
//        holder.txtDesc.text = comic.desc

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("coverImage", comic.image)
                putExtra("name", comic.name)
                putExtra("genres", comic.genre)
                putExtra("desc", comic.desc)
            }
            context.startActivity(intent)
        }
    }
}
