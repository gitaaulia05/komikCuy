package com.example.uts.Adapter

import com.example.uts.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.uts.Model.Pengajuan
import androidx.recyclerview.widget.RecyclerView

class PengajuanAdapter(
    private val data: List<Pengajuan>,
    private val onStatusChange: (Pengajuan, String) -> Unit
) : RecyclerView.Adapter<PengajuanAdapter.PengajuanViewHolder>() {

    class PengajuanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tvJudulPengajuan)
        val tvDeskripsi: TextView = view.findViewById(R.id.tvDeskripsiMasalah)
        val tvJenis: TextView = view.findViewById(R.id.tvJenisMasalah)
        val spinnerStatus: Spinner = view.findViewById(R.id.spinnerStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PengajuanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pengajuan, parent, false)
        return PengajuanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PengajuanViewHolder, position: Int) {
        val item = data[position]
        holder.tvJudul.text = item.judul_pengajuan
        holder.tvDeskripsi.text = item.deskripsi_masalah
        holder.tvJenis.text = item.jenis_masalah

        val statusOptions = listOf("Diterima", "Diproses", "Selesai")
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerStatus.adapter = adapter
        holder.spinnerStatus.setSelection(statusOptions.indexOf(item.status))

        holder.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val newStatus = statusOptions[pos]
                if (newStatus != item.status) {
                    onStatusChange(item, newStatus)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun getItemCount(): Int = data.size
}
