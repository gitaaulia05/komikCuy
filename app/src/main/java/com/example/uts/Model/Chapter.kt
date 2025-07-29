package com.example.uts.Model

import kotlinx.serialization.Serializable

@Serializable
data class Chapter (
    val id_chapter: Int? = null,
    val id_komik: Int,
    val nama_chapter: String ="",
    val created_at: String = ""
)