package com.example.uts.Model

import kotlinx.serialization.Serializable

@Serializable
data class Bookmark(
    val id_user: String?,
    val id_komik: Int
)