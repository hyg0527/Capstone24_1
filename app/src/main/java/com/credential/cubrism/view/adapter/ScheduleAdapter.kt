package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListScheduleBinding
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.view.diff.ScheduleDiffUtil
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertDateTimeFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
//            binding.txtTitle.text = item.title
//            binding.txtContent.text = item.content
//
//            binding.txtDate.text = if (item.allDay) {
//                convertDateTimeFormat(item.startDate, "yyyy-MM-dd'T'HH:mm", "MM.dd")
//            } else {
//                val startDateTime = convertDateTimeFormat(item.startDate, "yyyy-MM-dd'T'HH:mm", "MM.dd  h:mm a")
//                val endDateTime = item.endDate?.let { convertDateTimeFormat(it, "yyyy-MM-dd'T'HH:mm", "MM.dd  h:mm a") }
//                "$startDateTime\n~\n$endDateTime"
//            }
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
}