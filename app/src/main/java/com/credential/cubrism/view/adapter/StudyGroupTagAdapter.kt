package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListStudyTagBinding
import com.credential.cubrism.view.diff.StudyGroupTagDiffUtil

class StudyGroupTagAdapter : RecyclerView.Adapter<StudyGroupTagAdapter.ViewHolder>() {
    private var itemList = mutableListOf<String>()

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
        fun bind(item: String) {
            binding.txtTag.text = item
        }
    }

    fun setItemList(list: List<String>) {
        val diffCallBack = StudyGroupTagDiffUtil(itemList, list)
        val diffUtil = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffUtil.dispatchUpdatesTo(this)
    }
}