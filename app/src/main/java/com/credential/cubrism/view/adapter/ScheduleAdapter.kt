package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListScheduleBinding
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.view.diff.ScheduleDiffUtil
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertDateTimeFormat
import java.time.LocalDate
import java.util.Locale

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
                binding.timeStart.text = convertDateTimeFormat(item.startDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd", Locale.ENGLISH)
                binding.timeEnd.text = convertDateTimeFormat(item.endDate, "yyyy-MM-dd'T'HH:mm", "yyyy.MM.dd", Locale.ENGLISH)
            } else {
                binding.timeStart.text = convertDateTimeFormat(item.startDate, "yyyy-MM-dd'T'HH:mm", "yy.MM.dd  hh:mm a", Locale.ENGLISH)
                binding.timeEnd.text = convertDateTimeFormat(item.endDate, "yyyy-MM-dd'T'HH:mm", "yy.MM.dd  hh:mm a", Locale.ENGLISH)
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

    fun setItemListDay(list: List<ScheduleListDto>, yearMonthDay: LocalDate) {
        val filteredList = list.filter { schedule ->
            val startDate = LocalDate.parse(convertDateTimeFormat(schedule.startDate, "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd", Locale.ENGLISH))
            val endDate = LocalDate.parse(convertDateTimeFormat(schedule.endDate, "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd", Locale.ENGLISH))

            !(yearMonthDay.isBefore(startDate) || yearMonthDay.isAfter(endDate))
        }

        setItemList(filteredList)
    }
}