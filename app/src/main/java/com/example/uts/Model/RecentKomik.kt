package com.example.uts.Model

data class RecentKomik (
    val id_komik: Int,
    val judul_komik: String,
    val gambar_komik : String,
    val genre : String,
    val desc : String,
    val created_at: String? = null
)