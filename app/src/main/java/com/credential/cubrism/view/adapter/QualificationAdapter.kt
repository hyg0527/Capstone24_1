package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemDialogSearchBinding
import com.credential.cubrism.model.dto.QualificationListDto
import com.credential.cubrism.view.diff.QualificationDiffUtil

class QualificationAdapter : RecyclerView.Adapter<QualificationAdapter.ViewHolder>() {
    private var itemList = mutableListOf<QualificationListDto>()
    private var onItemClickListener: ((QualificationListDto, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDialogSearchBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemDialogSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: QualificationListDto) {
            binding.nameSearch.text = item.name
        }
    }

    fun setOnItemClickListener(listener: (QualificationListDto, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<QualificationListDto>) {
        val diffCallback = QualificationDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}