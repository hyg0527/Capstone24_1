package com.credential.cubrism.view.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemStudygroupRankBinding
import com.credential.cubrism.model.dto.MembersDto
import com.credential.cubrism.view.diff.StudyGroupRankDiffUtilUtil

class StudyGroupRankAdapter : RecyclerView.Adapter<StudyGroupRankAdapter.ViewHolder>() {
    private var itemList = mutableListOf<MembersDto>()

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStudygroupRankBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemStudygroupRankBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MembersDto) {
            val percentage = item.userGoal.completionPercentage?.toInt() ?: 0

            // 모션 생성
            ObjectAnimator.ofInt(binding.progressBar, "progress", 0, percentage).apply {
                duration = 800
                start()
            }

            Glide.with(binding.root)
                .load(item.profileImage)
                .error(R.drawable.profile_blue)
                .fallback(R.drawable.profile_blue)
                .dontAnimate()
                .into(binding.imgProfile)
            binding.txtNickname.text = item.nickname
            binding.progressBar.progress = percentage
            binding.txtPercentage.text = "$percentage%"
        }
    }

    fun setItemList(list: List<MembersDto>) {
        val diffCallback = StudyGroupRankDiffUtilUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun refreshAllItems() {
        for (position in itemList.indices) {
            notifyItemChanged(position)
        }
    }
}