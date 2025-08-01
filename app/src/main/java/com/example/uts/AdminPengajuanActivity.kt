package com.example.uts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uts.Model.Pengajuan
import com.example.uts.SupabaseClientProvider
import com.example.uts.Adapter.PengajuanAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class AdminPengajuanActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pengajuanAdapter: PengajuanAdapter
    private val pengajuanList = mutableListOf<Pengajuan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_pengajuan)

        recyclerView = findViewById(R.id.rvPengajuan)
        pengajuanAdapter = PengajuanAdapter(pengajuanList) { pengajuan, newStatus ->
            updateStatusPengajuan(pengajuan.id_pengajuan, newStatus)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminPengajuanActivity)
            adapter = pengajuanAdapter
        }

        fetchDataPengajuan()
    }

    private fun fetchDataPengajuan() {
        lifecycleScope.launch {
            try {
                val data = SupabaseClientProvider.client.postgrest["pengajuan_cs"]
                    .select().decodeList<Pengajuan>()

                pengajuanList.clear()
                pengajuanList.addAll(data)
                pengajuanAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Toast.makeText(this@AdminPengajuanActivity, "Gagal memuat data: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateStatusPengajuan(id: String, statusBaru: String) {
        lifecycleScope.launch {
            try {
                SupabaseClientProvider.client.postgrest.from("pengajuan_cs")
                    .update(
                        {
                            set("status", statusBaru)
                        },
                        {
                            filter {
                                eq("id_pengajuan", id)
                            }
                        }
                    )
                Toast.makeText(this@AdminPengajuanActivity, "Status diperbarui", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@AdminPengajuanActivity, "Gagal update status: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
