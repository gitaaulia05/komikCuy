package com.example.uts

data class RecentKomik (
    val id_komik: Int,
    val judul_komik: String,
    val gambar_komik : String,
    val created_at: String? = null
)