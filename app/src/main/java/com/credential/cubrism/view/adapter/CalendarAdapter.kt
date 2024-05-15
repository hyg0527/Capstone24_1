package com.credential.cubrism.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemCalendarDayBinding
import com.credential.cubrism.databinding.ItemCalendarWeekBinding
import com.credential.cubrism.model.dto.ScheduleListDto
import com.credential.cubrism.view.diff.CalendarDiffUtil
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertDateTimeFormat
import java.time.LocalDate
import java.util.Calendar

data class DateSelect(
    val date: String? = null,
    val year: Int = -1,
    val month: Int = -1,
    var isScheduled: Boolean = false,
    var isHighlighted: Boolean = false,
    var dayOfWeek: Int = -1
)

class CalendarAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = mutableListOf<DateSelect>()
    private var onItemClickListener: ((DateSelect, Int) -> Unit)? = null

    companion object {
        private const val VIEW_TYPE_WEEK = 0
        private const val VIEW_TYPE_DAY = 1
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int = if (position < 7) VIEW_TYPE_WEEK else VIEW_TYPE_DAY

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_WEEK) {
            val binding = ItemCalendarWeekBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            WeekViewHolder(binding)
        } else {
            val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DayViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WeekViewHolder) {
            holder.bind(itemList[position])
        } else if (holder is DayViewHolder) {
            holder.bind(itemList[position])
        }
    }

    inner class WeekViewHolder(private val binding: ItemCalendarWeekBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DateSelect) {
            binding.txtWeek.apply {
                text = item.date
                setTextColor(when (item.date) {
                    "일" -> Color.RED
                    "토" -> Color.BLUE
                    else -> Color.BLACK
                })
            }
        }
    }

    inner class DayViewHolder(private val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: DateSelect) {
            binding.txtDay.apply {
                text = item.date
                setTextColor(
                    when (item.dayOfWeek) {
                        Calendar.SUNDAY -> Color.RED
                        Calendar.SATURDAY -> Color.BLUE
                        else -> Color.BLACK
                    }
                )
            }

            binding.root.setBackgroundResource(
                if (item.isHighlighted)
                    R.drawable.date_highlighted
                else
                    android.R.color.transparent
            )

            binding.imgDot.visibility = if (item.isScheduled) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    fun setOnItemClickListener(listener: (DateSelect, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<DateSelect>) {
        val diffCallBack = CalendarDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    // 이전에 선택된 아이템의 배경을 없애고 새로 선택된 아이템의 배경을 설정
    fun setHighlightItem(item: DateSelect, position: Int) {
        val previousHighlightedPosition = itemList.indexOfFirst { it.isHighlighted }
        if (previousHighlightedPosition != -1) {
            itemList[previousHighlightedPosition].isHighlighted = false
            notifyItemChanged(previousHighlightedPosition)
        }
        item.isHighlighted = true
        notifyItemChanged(position)
    }

    // 일정이 있는 날짜에 점 표시
    fun setScheduledItem(scheduleList: List<ScheduleListDto>) {
        for (schedule in scheduleList) {
            val startDateStr = convertDateTimeFormat(schedule.startDate, "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd")
            val endDateStr = convertDateTimeFormat(schedule.endDate, "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd")

            val startDate = LocalDate.parse(startDateStr)
            val endDate = LocalDate.parse(endDateStr)

            itemList.filterIndexed { index, item ->
                index >= 7 && item.date != null // 요일과 공백 부분 제외
            }.forEach { item ->
                val itemDate = LocalDate.of(item.year, item.month, item.date!!.toInt())

                if (!itemDate.isBefore(startDate) && !itemDate.isAfter(endDate)) {
                    item.isScheduled = true
                    notifyItemChanged(itemList.indexOf(item))
                }
            }
        }
    }

    fun clearScheduledItem() {
        itemList.filterIndexed { index, item ->
            index >= 7 && item.date != null // 요일과 공백 부분 제외
        }.forEach { item ->
            if (item.isScheduled) {
                item.isScheduled = false
                notifyItemChanged(itemList.indexOf(item))
            }
        }
    }
}