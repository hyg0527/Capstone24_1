package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListGoalManageBinding
import com.credential.cubrism.model.dto.StudyGroupGoalSubmitListDto

class StudyGroupGoalAcceptAdapter : RecyclerView.Adapter<StudyGroupGoalAcceptAdapter.ViewHolder>() {
    private val items = mutableListOf<StudyGroupGoalSubmitListDto>()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListGoalManageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(private val binding: ItemListGoalManageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyGroupGoalSubmitListDto) {
            binding.goalacceptTitle.text = item.goalName
            binding.goalAcceptDescription.text = item.content
        }
    }

    fun setItem(item: StudyGroupGoalSubmitListDto) {
        items.add(item)
        notifyDataSetChanged()
    }
}