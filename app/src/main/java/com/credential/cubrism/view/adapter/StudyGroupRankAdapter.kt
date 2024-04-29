package com.credential.cubrism.view.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemStudygroupRankBinding

data class Rank(val userName: String? = null, val percentage: Int? = null)

class StudyGroupRankAdapter(private val items: ArrayList<Rank>) : RecyclerView.Adapter<StudyGroupRankAdapter.RankViewHolder>() {
    class RankViewHolder(private val binding: ItemStudygroupRankBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Rank) {
            val perStringTxt = "${item.percentage}%"
            val finalProgress = (item.percentage ?: 0).toInt()
            // 모션 생성
            val animation = ObjectAnimator.ofInt(binding.progressBar, "progress", 0, finalProgress)
            animation.duration = 800
            animation.start()

            binding.txtUserNameRank.text = item.userName
            binding.progressBar.progress = finalProgress
            binding.txtPercentageRank.text = perStringTxt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStudygroupRankBinding.inflate(inflater, parent, false)
        return RankViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun reloadItems() {
        notifyDataSetChanged()
    }
}