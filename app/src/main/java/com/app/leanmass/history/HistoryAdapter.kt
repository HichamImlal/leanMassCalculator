package com.app.leanmass.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.leanmass.model.LBMResult
import com.app.leanmass.databinding.ItemHistoryBinding
import java.util.Locale

class HistoryAdapter(private var records: List<LBMResult>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val record = records[position]
        holder.binding.tvItemLbm.text = String.format(Locale.getDefault(), "%.2f kg", record.lbmResultat)
        holder.binding.tvItemDetails.text = String.format(
            Locale.getDefault(),
            "Poids : %.1f kg | Taille : %.1f cm | %s",
            record.poids, record.taille, record.sexe
        )
    }

    override fun getItemCount(): Int = records.size

    fun updateData(newRecords: List<LBMResult>) {
        this.records = newRecords
        notifyDataSetChanged()
    }
}
