package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListStudyTagBinding
import com.credential.cubrism.view.diff.StudyGroupTagDiffUtil

class StudyGroupTagAdapter : RecyclerView.Adapter<StudyGroupTagAdapter.ViewHolder>() {
    private var itemList = mutableListOf<String>()
    private var onItemClickListener: ((String, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListStudyTagBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListStudyTagBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: String) {
            binding.txtTag.text = item
        }
    }

    fun setOnItemClickListener(listener: (String, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<String>) {
        val diffCallBack = StudyGroupTagDiffUtil(itemList, list)
        val diffUtil = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffUtil.dispatchUpdatesTo(this)
    }
}