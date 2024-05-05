package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListStudyTag3Binding

interface TagDeleteClickListener {
    fun onDeleteClick(position: Int)
}

class StudyGroupTagAdapter2(private val listener: TagDeleteClickListener) : RecyclerView.Adapter<StudyGroupTagAdapter2.ViewHolder>() {
    private var itemList = mutableListOf<String>()

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListStudyTag3Binding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListStudyTag3Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.txtTag.text = item
            binding.imgDelete.setOnClickListener {
                listener.onDeleteClick(adapterPosition)
            }
        }
    }

    fun addItem(item: String) {
        itemList.add(item)
        notifyItemInserted(itemList.size - 1)
    }

    fun deleteItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItemList(): List<String> {
        return itemList
    }

    fun getItemSize(): Int {
        return itemList.size
    }
}