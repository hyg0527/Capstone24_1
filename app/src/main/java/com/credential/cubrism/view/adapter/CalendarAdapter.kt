package com.credential.cubrism.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDate
import java.time.format.DateTimeFormatter


interface DateMonthClickListener {
    fun onItemClicked(item: DateSelect)
}
data class DateSelect(var date: String, var isScheduled: Boolean = false, var isHighlighted: Boolean = false)

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.MonthViewHolder>() {
    private var items = arrayListOf<DateSelect>()

    private var itemClickListener: DateMonthClickListener? = null
    private var isBackgroundSet = false
    fun setItemClickListener(listener: DateMonthClickListener) {
        itemClickListener = listener
    }

    inner class MonthViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val date = v.findViewById<TextView>(R.id.calMonthDate)
        val isScheduled = v.findViewById<CircleImageView>(R.id.isScheduledCircle)
        init {
            v.setOnClickListener {
                val position = adapterPosition
                val item = items[position]
                itemClickListener?.onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_month_date, parent, false)

        return MonthViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val currentItem = items[position]
        holder.date.text = currentItem.date

        if (!isBackgroundSet && currentItem.date.equals(getCurrentDayToString())) {
            isBackgroundSet = true // 처음 로드 될때 오늘 날짜 하이라이팅
            holder.itemView.setBackgroundResource(R.drawable.date_highlighted)
        } else {
            holder.itemView.setBackgroundResource(0)
        }

        val typeface = ResourcesCompat.getFont(holder.date.context, R.font.godom) // 요일만 폰트 변경
        if (holder.date.text.length == 3) {
            holder.date.typeface = typeface

            val layoutParams = holder.itemView.layoutParams
            layoutParams.height = 80 // 변경할 높이 값
            holder.itemView.layoutParams = layoutParams
        }


        if (!items[position].isScheduled)   //저장된 일정부분이 없으면 점표시 없애기(초기화 부분)
            holder.isScheduled.visibility = View.GONE

        if (items[position].isHighlighted)
            holder.itemView.setBackgroundResource(R.drawable.date_highlighted)

        if (position % 7 == 0) {
            holder.date.setTextColor(Color.RED)
        } else if (position % 7 == 6) {
            holder.date.setTextColor(Color.BLUE)
        } else {
            holder.date.setTextColor(Color.BLACK)
        }
    }

    private fun getCurrentDayToString(): String {
        val currentDate = LocalDate.now()
        // DateTimeFormatter를 사용 하여 날짜를 원하는 형식의 문자열로 변환
        val formatter = DateTimeFormatter.ofPattern("d")

        return currentDate.format(formatter)
    }

    fun updateCalendar(monthList: ArrayList<DateSelect>) { // 데이터 갱신 함수
        this.items = monthList
        notifyDataSetChanged()
    }

    fun highlightCurrentDate(selectedItem: DateSelect, isHighlighted: Boolean) {
        for (item in items) {
            item.isHighlighted = false
        }
        selectedItem.isHighlighted = isHighlighted
        notifyDataSetChanged()
    }

    fun highlightDate(dateString: String) { // 현재 선택된 날짜 highlighting 함수
        val (_, date) = convertDateStringAndInt(dateString)
        for (item in items) {
            if ((item.date).isDigitsOnly() && (item.date).toInt() == date)
                item.isHighlighted = true
        }
        notifyDataSetChanged()
    }

    private fun convertDateStringAndInt(dateString: String): Pair<String, Int> { // 연월을 string(0000 - 00), 일을 int(0)으로 반환
        val yearMonth = dateString.take(9)
        val dateInt = dateString.substring(12, 14).toInt()

        return Pair(yearMonth, dateInt)
    }
}