package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListHomeTodaylistBinding
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.view.diff.ScheduleDiffUtil
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertDateTimeFormat
import java.time.LocalDate

// home 화면의 todoList adapter.
class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private val itemList = mutableListOf<ScheduleListDto>()

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListHomeTodaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(val binding: ItemListHomeTodaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ScheduleListDto) {
            binding.txtSchedule.text = item.title
        }
    }

    fun setItemList(list: List<ScheduleListDto>) {
        val today = LocalDate.now()

        val filteredList = list.filter { schedule ->
            val startDate = LocalDate.parse(convertDateTimeFormat(schedule.startDate, "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd"))
            val endDate = LocalDate.parse(convertDateTimeFormat(schedule.endDate, "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd"))

            !(today.isBefore(startDate) || today.isAfter(endDate))
        }

        val diffCallback = ScheduleDiffUtil(itemList, filteredList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(filteredList)
        diffResult.dispatchUpdatesTo(this)
    }
}