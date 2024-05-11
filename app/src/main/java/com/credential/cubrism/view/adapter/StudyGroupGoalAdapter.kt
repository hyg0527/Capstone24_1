package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListGoalBinding
import com.credential.cubrism.databinding.ItemListGoalshowBinding
import com.credential.cubrism.model.dto.StudyGroupGoalListDto
import com.credential.cubrism.view.diff.StudyGroupGoalDiffUtil

enum class StudyGroupGoalType {
    GOAL_LIST,
    GOAL_MANAGE
}

interface StudyGroupGoalClickListener {
    fun onGoalClick(item: StudyGroupGoalListDto)
}

class StudyGroupGoalAdapter(private val goalType: StudyGroupGoalType, private val listener: StudyGroupGoalClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = mutableListOf<StudyGroupGoalListDto>()

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
        fun bind(item: StudyGroupGoalListDto) {

        }
    }

    inner class GoalViewHolder(private val binding: ItemListGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyGroupGoalListDto) {
            binding.txtCount.text = "목표 ${item.index}"
            binding.txtTitle.text = item.goalName
            binding.btnDelete.setOnClickListener {
                listener.onGoalClick(item)
            }
        }
    }

    fun setItemList(list: List<StudyGroupGoalListDto>) {
        val diffCallback = StudyGroupGoalDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemSize(): Int = itemList.size
}