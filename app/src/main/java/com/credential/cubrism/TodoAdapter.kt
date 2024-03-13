package com.credential.cubrism

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


data class TodayData(var todayCheck: Boolean? = null, val toaySchedule: String? = null) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(todayCheck)
        parcel.writeString(toaySchedule)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TodayData> {
        override fun createFromParcel(parcel: Parcel): TodayData {
            return TodayData(parcel)
        }

        override fun newArray(size: Int): Array<TodayData?> {
            return arrayOfNulls(size)
        }
    }

}

class TodoAdapter(private val items: ArrayList<TodayData>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val checkTodo = v.findViewById<CheckBox>(R.id.todayCheckBox)
        val todoSchedule = v.findViewById<TextView>(R.id.todaySchedule)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_list_home_todaylist, parent, false)
        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = items[position]

        holder.checkTodo.isChecked = false
        holder.todoSchedule.text = items[position].toaySchedule

        holder.checkTodo.setOnCheckedChangeListener { buttonView, isChecked ->
            holder.checkTodo.isChecked = isChecked
        }

    }


    fun addItem(item: TodayData) {
        items.add(item)
    }

    fun clearItem() {
        items.clear()
    }
}