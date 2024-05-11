package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListGoalBinding
import com.credential.cubrism.databinding.ItemListGoalshowBinding

data class Goals(val id: Int? = null, val name: String? = null, var num: Int? = null)

class GoalAdapter(private val items: ArrayList<Goals>, private val isList: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class GoalViewHolder(val binding: ItemListGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                val item = items[position]

                Toast.makeText(itemView.context, "목표가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                items.remove(item)

                rearrangeItem(items)
                notifyDataSetChanged()
            }
        }
    }
    inner class GoalListViewHolder(val binding: ItemListGoalshowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnGoalStart.setOnClickListener {
                Toast.makeText(itemView.context, "\'${items[adapterPosition].name}\'을 달성하였습니다!", Toast.LENGTH_SHORT).show()
                binding.btnGoalStart.isEnabled = false
                binding.btnGoalStart.text = "학습완료"

                val color = ContextCompat.getColor(itemView.context, R.color.gray)
                binding.btnGoalStart.setBackgroundColor(color)
                notifyDataSetChanged()
            }
        }
    }

    companion object {
        private const val MANAGE = 0
        private const val LIST = 1
    }
    override fun getItemViewType(position: Int): Int {
        return if (!isList) MANAGE else LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MANAGE -> {
                val binding = ItemListGoalBinding.inflate(inflater, parent, false)
                GoalViewHolder(binding)
            }
            LIST -> {
                val binding = ItemListGoalshowBinding.inflate(inflater, parent, false)
                GoalListViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GoalViewHolder -> {
                val item = items[position]
                holder.binding.txtTitle.setText(item.name)
                holder.binding.txtCount.text = "목표${item.num}"
            }
            is GoalListViewHolder -> {
                val item = items[position]
                holder.binding.txtGoalTitleList.text = item.name
            }
        }
    }

    fun addItem(itemNum: Int, text: String) {
        items.add(Goals(itemNum, text, itemNum))
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