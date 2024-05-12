package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListHomeTodaylistBinding
import com.credential.cubrism.model.dto.ScheduleListDto


// home 화면의 todoList adapter.
class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    private val items = arrayListOf<ScheduleListDto>()

    inner class TodoViewHolder(val binding: ItemListHomeTodaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ScheduleListDto) {
            binding.todaySchedule.text = item.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemListHomeTodaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setTodayItemList(list: ArrayList<ScheduleListDto>) {
        items.clear()
        items.addAll(list)

        notifyDataSetChanged()
    }
}