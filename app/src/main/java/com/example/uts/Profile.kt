package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.Adapter.RecentAdapter
import com.example.uts.Model.RecentKomik
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Profile : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var btnAdminDashboard: Button
    private lateinit var recyclerViewBookmark: RecyclerView
    private lateinit var bookmarkAdapter: RecentAdapter
    private val bookmarkedComics = mutableListOf<RecentKomik>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        bottomNavigationView = findViewById(R.id.bottom_nav)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val username: TextView = findViewById(R.id.username)


        recyclerViewBookmark = findViewById(R.id.recyclerViewBookmark)
//        recyclerViewBookmark.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewBookmark.layoutManager = GridLayoutManager(this, 2)
        bookmarkAdapter = RecentAdapter(bookmarkedComics)
        recyclerViewBookmark.adapter = bookmarkAdapter

        lifecycleScope.launch {
            val repository = UserPreferencesRepository(applicationContext.dataStore)
            val userName = repository.getUserName()
            Log.d("Profile", "User Name: $userName")

            runOnUiThread {
                username.text = userName ?: "Guest"
            }
        }

        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    SupabaseClientProvider.client.auth.signOut()
                    val repository = UserPreferencesRepository(applicationContext.dataStore)
                    repository.clearLoginData()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Profile, "Sampai Jumpa!", Toast.LENGTH_SHORT).show()
                        Log.d("Profile", "Logout successful")
                        val intent = Intent(this@Profile, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Profile, "Gagal logout: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("Profile", "Error during logout", e)
                    }
                }
            }
        }

        btnAdminDashboard = findViewById(R.id.btnAdminDashboard)
        btnAdminDashboard.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        }

        fetchBookmarkedComics()
    }

    override fun onResume() {
        super.onResume()
        buttonNavigation.setup(bottomNavigationView, this, R.id.menu_profile)
        fetchBookmarkedComics()
    }

    private fun fetchBookmarkedComics() {
        lifecycleScope.launch {
            try {
                val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                if (userId == null) {
                    Toast.makeText(this@Profile, "User  harus login.", Toast.LENGTH_SHORT).show()
                    bookmarkedComics.clear()
                    bookmarkAdapter.notifyDataSetChanged()
                    return@launch
                }

                val responseBookmarks = SupabaseClientProvider.client.postgrest
                    .from("bookmark")
                    .select {
                        filter {
                            eq("id_user", userId)
                        }
                    }

                val gson = Gson()
                val bookmarkListType = object : TypeToken<List<com.example.uts.Model.Bookmark>>() {}.type
                val bookmarks: List<com.example.uts.Model.Bookmark> = gson.fromJson(responseBookmarks.data.toString(), bookmarkListType)

                val comicIds = bookmarks.map { it.id_komik }

                if (comicIds.isNotEmpty()) {
                    val comics = mutableListOf<RecentKomik>()

                    for (comicId in comicIds) {
                        val responseComic = SupabaseClientProvider.client.postgrest
                            .from("komik")
                            .select {
                                filter {
                                    eq("id_komik", comicId)
                                }
                            }

                        val comicListType = object : TypeToken<List<RecentKomik>>() {}.type
                        val comic: List<RecentKomik> = gson.fromJson(responseComic.data.toString(), comicListType)

                        if (comic.isNotEmpty()) {
                            comics.addAll(comic)
                        }
                    }

                    bookmarkedComics.clear()
                    bookmarkedComics.addAll(comics)
                    bookmarkAdapter.notifyDataSetChanged()
                } else {
                    bookmarkedComics.clear()
                    bookmarkAdapter.notifyDataSetChanged()
                    Toast.makeText(this@Profile, "No bookmarked comics yet.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("Profile", "Error fetching bookmarked comics: ${e.message}", e)
                Toast.makeText(this@Profile, "Failed to load bookmarked comics: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}