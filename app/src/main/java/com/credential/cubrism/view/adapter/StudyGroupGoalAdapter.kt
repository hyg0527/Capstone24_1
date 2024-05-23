package com.credential.cubrism.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListGoalBinding
import com.credential.cubrism.databinding.ItemListGoalshowBinding
import com.credential.cubrism.model.dto.GoalsDto
import com.credential.cubrism.view.diff.StudyGroupGoalDiffUtil

enum class StudyGroupGoalType {
    GOAL_LIST,
    GOAL_MANAGE
}

class StudyGroupGoalAdapter(private val goalType: StudyGroupGoalType, private val listener: OnViewClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnViewClickListener {
        fun setOnViewClick(item: GoalsDto)
    }

    private var itemList = mutableListOf<GoalsDto>()

    companion object {
        private const val VIEW_TYPE_GOAL_LIST = 0
        private const val VIEW_TYPE_GOAL_MANAGE = 1
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return when (goalType) {
            StudyGroupGoalType.GOAL_LIST -> VIEW_TYPE_GOAL_LIST
            StudyGroupGoalType.GOAL_MANAGE -> VIEW_TYPE_GOAL_MANAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GOAL_LIST) {
            val binding = ItemListGoalshowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GoalListViewHolder(binding)
        } else {
            val binding = ItemListGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GoalViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GoalListViewHolder) {
            holder.bind(itemList[position])
        } else if (holder is GoalViewHolder) {
            holder.bind(itemList[position])
        }
    }

    inner class GoalListViewHolder(private val binding: ItemListGoalshowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GoalsDto) {
            binding.txtTitle.text = item.goalName
            binding.btnFinish.apply {
                if (item.completed) {
                    text = "학습완료"
                    setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.gray))
                    setTextColor(Color.parseColor("#989898"))
                    isEnabled = false
                } else if (item.submitted) {
                    text = "제출완료"
                    setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.gray))
                    setTextColor(Color.parseColor("#989898"))
                    isEnabled = false
                } else {
                    text = "학습완료"
                    setBackgroundColor(Color.parseColor("#B49FF0"))
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                    isEnabled = true
                }

                setOnClickListener {
                    listener.setOnViewClick(item)
                }
            }
        }
    }

    inner class GoalViewHolder(private val binding: ItemListGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GoalsDto) {
            binding.txtCount.text = "목표 ${item.index}"
            binding.txtTitle.text = item.goalName
            binding.btnDelete.setOnClickListener {
                listener.setOnViewClick(item)
            }
        }
    }

    fun setItemList(list: List<GoalsDto>) {
        val diffCallback = StudyGroupGoalDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemSize(): Int = itemList.size
}