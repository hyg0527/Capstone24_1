package com.credential.cubrism

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(private var items: ArrayList<String>) : RecyclerView.Adapter<CalendarAdapter.MonthViewHolder>() {
    inner class MonthViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val date = v.findViewById<TextView>(R.id.calMonthDate)
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
        holder.date.text = items[position]

        if (position % 7 == 0) {
            holder.date.setTextColor(Color.RED)
        }
        else if (position % 7 == 6) {
            holder.date.setTextColor(Color.BLUE)
        }
        else {
            holder.date.setTextColor(Color.BLACK)
        }
    }

    fun updateCalendar(monthList: ArrayList<String>) { // 데이터 갱신 함수
        this.items = monthList
        notifyDataSetChanged()
    }
}