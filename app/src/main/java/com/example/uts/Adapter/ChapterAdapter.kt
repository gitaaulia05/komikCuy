package com.example.uts.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.Model.Chapter
import com.example.uts.R

class ChapterAdapter(private val chapterList: List<Chapter>) :
    RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtChapterTitle: TextView = view.findViewById(R.id.txtChapterTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = chapterList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = chapterList[position]
        holder.txtChapterTitle.text = chapter.nama_chapter
    }
}
