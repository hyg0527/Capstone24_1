package com.credential.cubrism.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R

interface DateWeekClickListener {
    fun onItemClick(item: DateWeek)
}

data class DateWeek(val date: Int
? = null, val dayOfTheWeek: String? = null)

class CalWeekAdapter(private var items: ArrayList<DateWeek>) : RecyclerView.Adapter<CalWeekAdapter.WeekViewHolder>() {
    private var itemClickListener: DateWeekClickListener? = null
    fun setItemClickListener(listener: DateWeekClickListener) {
        itemClickListener = listener
    }

    inner class WeekViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val txtDayOfTheWeek = v.findViewById<TextView>(R.id.txtDayOfTheWeek)
        val txtDate = v.findViewById<TextView>(R.id.txtDate)
        init {
            v.setOnClickListener {
                val position = adapterPosition
                val clickedItem = items[position]
                itemClickListener?.onItemClick(clickedItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_week_date, parent, false)

        return WeekViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val dayOfTheWeek = holder.txtDayOfTheWeek
        val txtDate = holder.txtDate

        dayOfTheWeek.text = items[position].dayOfTheWeek
        txtDate.text = items[position].date.toString()

        if (dayOfTheWeek.text.equals("Sat")) { // 토, 일 색상구분
            dayOfTheWeek.setTextColor(Color.BLUE)
            txtDate.setTextColor(Color.BLUE)
        } else if (dayOfTheWeek.text.equals("Sun")) {
            dayOfTheWeek.setTextColor(Color.RED)
            txtDate.setTextColor(Color.RED)
        } else {
            dayOfTheWeek.setTextColor(Color.BLACK)
            txtDate.setTextColor(Color.BLACK)
        }
    }

    fun updateCalendar(weekList: ArrayList<DateWeek>) { // 데이터 갱신 함수
        this.items = weekList
        notifyDataSetChanged()
    }
}