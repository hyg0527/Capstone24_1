package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

data class CalMonth(val date: String? = null, val title: String? = null, val info: String? = null,
                    val time: String? = null, val isFullTime: Boolean)

class CalMonthListAdapter(private var items: ArrayList<CalMonth>) : RecyclerView.Adapter<CalMonthListAdapter.CalViewHolder>() {
    inner class CalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title = v.findViewById<TextView>(R.id.txtCalMonthTitle)
        val time = v.findViewById<TextView>(R.id.txtCalMonthTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list_calmonth, parent, false)

        return CalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: CalViewHolder, position: Int) {
        holder.title.text = items[position].title
        holder.time.text = items[position].time
    }

    fun updateList(date: String) { // 날짜에 맞는 일정만 화면에 출력하는 함수
        println("date: " + date)
        val newList = ArrayList<CalMonth>()
        newList.clear()

        for (item in items) {
            println("item.date: " + item.date)
            if (item.date.equals(date))
                newList.add(item)
        }

        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun addItem(item: CalMonth) { // 일정 항목 추가 함수
        items.add(item)
    }

    fun removeItem(item: CalMonth) {
        items.remove(item)
    }

    fun clearItem() {
        items.clear()
    }
}