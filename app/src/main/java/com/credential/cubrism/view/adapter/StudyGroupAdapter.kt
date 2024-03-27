package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListProgresBinding
import com.credential.cubrism.databinding.ItemListStudyBinding
import com.credential.cubrism.model.dto.GroupList
import com.credential.cubrism.view.diff.StudyGroupDiffUtil

class StudyGroupAdapter(private val recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = mutableListOf<GroupList>()
    private var onItemClickListener: ((GroupList, Int) -> Unit)? = null
    private var isLoading = false

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    override fun getItemCount(): Int {
        return if (isLoading) itemList.size + 1 else itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading && position == itemList.size) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemListStudyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        } else {
            val binding = ItemListProgresBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LoadingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(itemList[position])
        } else if (holder is LoadingViewHolder) {
            holder.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    inner class ViewHolder(private val binding: ItemListStudyBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: GroupList) {
            Glide.with(itemView.context).load(R.drawable.studymember).into(binding.imageView10)
            binding.txtStudyTitle.text = item.groupName
            binding.txtStudyInfo.text = item.groupDescription
            binding.textView41.text = "${item.currentMembers} / ${item.maxMembers}"
            if (item.recruiting) {
                binding.studyStatusIng.visibility = View.VISIBLE
                binding.studyStatusEnd.visibility = View.INVISIBLE
            } else {
                binding.studyStatusIng.visibility = View.INVISIBLE
                binding.studyStatusEnd.visibility = View.VISIBLE
            }
        }
    }

    inner class LoadingViewHolder(binding: ItemListProgresBinding) : RecyclerView.ViewHolder(binding.root) {
        val progressIndicator: View = binding.progressIndicator
    }

    fun setOnItemClickListener(listener: (GroupList, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<GroupList>) {
        val diffCallback = StudyGroupDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setLoading(isLoading: Boolean) {
        if (this.isLoading == isLoading) return

        val lastPosition = if (this.isLoading) itemList.size else itemList.size - 1
        recyclerView.scrollToPosition(lastPosition)

        val progressBarHeight = recyclerView.height / 10
        recyclerView.smoothScrollBy(0, progressBarHeight)

        this.isLoading = isLoading

        if (isLoading) {
            notifyItemInserted(itemList.size)
        } else {
            notifyItemRemoved(lastPosition)
        }
    }
}