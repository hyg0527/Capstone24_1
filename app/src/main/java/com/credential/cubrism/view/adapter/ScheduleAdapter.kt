package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListScheduleBinding
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.view.CalendarHyg
import com.credential.cubrism.view.diff.ScheduleDiffUtil
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertDateTimeFormat

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {
    private var itemList = mutableListOf<ScheduleListDto>()
    private var onItemClickListener: ((ScheduleListDto, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListScheduleBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: ScheduleListDto) {
            binding.txtCalMonthTitle.text = item.title
            binding.txtCalMonthInfo.text = item.content

            if (item.allDay) {
                binding.timeStart.text = convertDateTimeFormat(item.startDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd")
                binding.timeEnd.text = convertDateTimeFormat(item.endDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd")
            } else {
                binding.timeStart.text = convertDateTimeFormat(item.startDate, "yyyy-MM-dd'T'HH:mm", "yy.MM.dd  h:mm a")
                binding.timeEnd.text = convertDateTimeFormat(item.endDate, "yyyy-MM-dd'T'HH:mm", "yy.MM.dd  h:mm a")
            }
        }
    }

    fun setOnItemClickListener(listener: (ScheduleListDto, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<ScheduleListDto>) {
        val diffCallBack = ScheduleDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setItemListDay(list: List<ScheduleListDto>, yearMonthDay: Int) {
        val localList = mutableListOf<ScheduleListDto>()

        for (schedule in list) {
            val start = schedule.startDate.substringBefore("T").replace("-", "").toInt()
            val end = schedule.endDate.substringBefore("T").replace("-", "").toInt()

            if (yearMonthDay in start..end)
                localList.add(schedule)
        }
        setItemList(localList)
    }
}