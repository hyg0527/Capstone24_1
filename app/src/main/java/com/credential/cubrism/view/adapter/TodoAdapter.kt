package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// home 화면의 todoList adapter.
class TodoAdapter(private val items: ArrayList<CalMonth>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    inner class TodoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val checkTodo = v.findViewById<CheckBox>(R.id.todayCheckBox)
        val toDoSchedule = v.findViewById<TextView>(R.id.todaySchedule)
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
        holder.toDoSchedule.text = items[position].title
        holder.checkTodo.setOnCheckedChangeListener { _, isChecked ->
            holder.checkTodo.isChecked = isChecked
        }
    }
}