package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

data class Goals(val name: String? = null, val time: String? = null, var num: Int? = null)

class GoalAdapter(private val items: ArrayList<Goals>, val isList: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class GoalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = v.findViewById<EditText>(R.id.editTextGoalTitle)
        val time = v.findViewById<TextView>(R.id.txtGoalTime)
        val num = v.findViewById<TextView>(R.id.txtGoalCount)
        val removeBtn = v.findViewById<ImageButton>(R.id.closeBtn2)

        init {
            removeBtn.setOnClickListener {
                val position = adapterPosition
                val item = items[position]
                items.remove(item)

                rearrangeItem(items)
                notifyDataSetChanged()
            }
        }
    }
    inner class GoalListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = v.findViewById<TextView>(R.id.txtGoalTitleList)
        val startBtn = v.findViewById<Button>(R.id.btnGoalStart)

        init {
            startBtn.setOnClickListener {
                Toast.makeText(itemView.context, "학습을 시작합니다.", Toast.LENGTH_SHORT).show()
                startBtn.visibility = View.GONE

                notifyDataSetChanged()
            }
        }
    }

    companion object {
        private const val LIST = 1
        private const val MANAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if(!isList) MANAGE
        else LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            MANAGE -> {
                val view = inflater.inflate(R.layout.item_list_goal, parent, false)
                GoalViewHolder(view)
            }
            LIST -> {
                val view = inflater.inflate(R.layout.item_list_goalshow, parent, false)
                return GoalListViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type") // 예외 처리
        }

    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GoalViewHolder -> {
                holder.name.setText(items[position].name)
                holder.time.text = items[position].time
                holder.num.text = "목표${items[position].num}"
            }
            is GoalListViewHolder -> {
                holder.name.text = items[position].name
            }
        }
    }

    fun addItem(itemNum: Int) {
        items.add(Goals(null, "0시간 0분", itemNum))
        notifyDataSetChanged()
    }

    fun rearrangeItem(items: ArrayList<Goals>) {
        var i = 1
        for (item in items) {
            item.num = i
            i++
        }
    }

    fun getItem(): ArrayList<Goals> {
        return items
    }
}