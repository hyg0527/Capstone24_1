package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListMystudyBinding
import com.credential.cubrism.model.dto.GroupList
import com.credential.cubrism.view.diff.StudyGroupDiffUtil

class StudyGroupMyAdapter(private val listener: OnViewClickListener) : RecyclerView.Adapter<StudyGroupMyAdapter.ViewHolder>() {
    interface OnViewClickListener {
        fun setOnViewClick(item: GroupList)
    }

    private var itemList = mutableListOf<GroupList>()

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListMystudyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListMystudyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupList) {
            binding.txtTitle.text = item.groupName

            binding.btnEnter.setOnClickListener {
                listener.setOnViewClick(item)
            }
        }
    }

    fun setItemList(list: List<GroupList>) {
        val diffCallback = StudyGroupDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}