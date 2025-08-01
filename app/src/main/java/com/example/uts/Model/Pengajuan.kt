package com.example.uts.Model

import kotlinx.serialization.Serializable

@Serializable
data class Pengajuan(
    val id_pengajuan: String,
    val judul_pengajuan: String,
    val deskripsi_masalah: String,
    val jenis_masalah: String,
    val status: String
)
