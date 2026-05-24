package com.app.leanmass.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.leanmass.db.DatabaseHelper
import com.leanmass.calculator.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarHistory.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = HistoryAdapter(emptyList())
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        chargerHistorique()

        binding.btnViderHistorique.setOnClickListener {
            viderHistorique()
        }
    }

    private fun chargerHistorique() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = DatabaseHelper.getDatabase(this@HistoryActivity)
            val listeRecords = db.leanMassDao().getAllRecords()

            withContext(Dispatchers.Main) {
                adapter.updateData(listeRecords)
            }
        }
    }

    private fun viderHistorique() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = DatabaseHelper.getDatabase(this@HistoryActivity)
            db.leanMassDao().deleteAllRecords()

            withContext(Dispatchers.Main) {
                adapter.updateData(emptyList())
            }
        }
    }
}
