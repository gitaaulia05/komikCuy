package com.example.uts.Adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts.Model.RecentKomik
import com.example.uts.Model.Chapter
import com.example.uts.R
import com.example.uts.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminComicAdapter(
    private val comicList: MutableList<RecentKomik>,
    private val context: Context,
    private val onComicDeleted: (RecentKomik) -> Unit,
    private val onComicEdited: (RecentKomik) -> Unit,
    private val onAddChapterClicked: (RecentKomik) -> Unit
) : RecyclerView.Adapter<AdminComicAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvComicTitle: TextView = view.findViewById(R.id.tvComicTitle)
        val ivComicCover: ImageView = view.findViewById(R.id.ivComicCover)
        val tvComicGenre: TextView = view.findViewById(R.id.tvComicGenre)
        val tvChapterCount: TextView = view.findViewById(R.id.tvChapterCount)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnAddChapter: Button = view.findViewById(R.id.btnAddChapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_comic, parent, false)
        return ViewHolder(view)
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

    override fun getItemCount(): Int = comicList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comic = comicList[position]
        holder.tvComicTitle.text = comic.judul_komik
        holder.tvComicGenre.text = comic.genre

        Glide.with(holder.itemView.context)
            .load(comic.gambar_komik)
            .into(holder.ivComicCover)

        if (holder.itemView.context is AppCompatActivity) {
            (holder.itemView.context as AppCompatActivity).lifecycleScope.launch {
                val chapterCount = getChapterCountForComic(comic.id_komik)
                holder.tvChapterCount.text = "$chapterCount Chapter"
            }
        } else {
            holder.tvChapterCount.text = "N/A Chapters"
            Log.e("AdminComicAdapter", "Context is not AppCompatActivity, cannot use lifecycleScope")
        }


        holder.btnEdit.setOnClickListener {
            onComicEdited(comic)
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Comic")
                .setMessage("Are you sure you want to delete '${comic.judul_komik}'?")
                .setPositiveButton("Yes") { dialog, _ ->
                    deleteComic(comic, position)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        holder.btnAddChapter.setOnClickListener {
            onAddChapterClicked(comic)
        }
    }

    private fun deleteComic(comic: RecentKomik, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClientProvider.client.postgrest["komik"].delete {
                    filter {
                        eq("id_komik", comic.id_komik)
                    }
                }
                withContext(Dispatchers.Main) {
                    comicList.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(
                        context,
                        "${comic.judul_komik} deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    onComicDeleted(comic)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Failed to delete comic: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }
}