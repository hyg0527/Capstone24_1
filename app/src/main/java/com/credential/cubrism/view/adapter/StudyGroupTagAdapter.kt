package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListStudyTag1Binding
import com.credential.cubrism.databinding.ItemListStudyTag2Binding
import com.credential.cubrism.view.diff.StringListDiffUtil

class StudyGroupTagAdapter(private val tagStyle: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = mutableListOf<String>()

    companion object {
        private const val VIEW_TYPE_TAG1 = 0
        private const val VIEW_TYPE_TAG2 = 1
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int = if (tagStyle == 1) VIEW_TYPE_TAG1 else VIEW_TYPE_TAG2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TAG1) {
            val binding = ItemListStudyTag1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
            Tag1ViewHolder(binding)
        } else {
            val binding = ItemListStudyTag2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
            Tag2ViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Tag1ViewHolder) {
            holder.bind(itemList[position])
        } else if (holder is Tag2ViewHolder) {
            holder.bind(itemList[position])
        }
    }

    inner class Tag1ViewHolder(private val binding: ItemListStudyTag1Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.txtTag.text = item
        }
    }

    inner class Tag2ViewHolder(private val binding: ItemListStudyTag2Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.txtTag.text = item
        }
    }

    fun setItemList(list: List<String>) {
        val diffCallBack = StringListDiffUtil(itemList, list)
        val diffUtil = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffUtil.dispatchUpdatesTo(this)
    }
}