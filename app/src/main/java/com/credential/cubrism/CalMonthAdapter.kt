package com.credential.cubrism

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

interface DateMonthClickListener {
    fun onItemClicked(item: String)
}
data class DateSelect(var date: String? = null, var isScheduled: Boolean = false)

class CalendarAdapter(private var items: ArrayList<DateSelect>) : RecyclerView.Adapter<CalendarAdapter.MonthViewHolder>() {
    private var itemClickListener: DateMonthClickListener? = null
    fun setItemClickListener(listener: DateMonthClickListener) {
        itemClickListener = listener
    }

    inner class MonthViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val date = v.findViewById<TextView>(R.id.calMonthDate)
        val isScheduled = v.findViewById<CircleImageView>(R.id.isScheduledCircle)
        init {
            v.setOnClickListener {
                val position = adapterPosition
                val item = items[position].date ?: ""
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
        holder.date.text = items[position].date
        if (!items[position].isScheduled)
            holder.isScheduled.visibility = View.GONE

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

    fun updateCalendar(monthList: ArrayList<DateSelect>) { // 데이터 갱신 함수
        this.items = monthList
        notifyDataSetChanged()
    }

    fun updateScheduleDot(position: Int, isvisible: Boolean) {
        if (position in 0 until items.size) {
            items[position].isScheduled = isvisible
            notifyItemChanged(position)
        }
    }
}