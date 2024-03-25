package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListMiddlefieldBinding
import com.credential.cubrism.model.dto.MiddleFieldDto
import com.credential.cubrism.view.diff.MiddleFieldDiffUtil

class MiddleFieldAdapter : RecyclerView.Adapter<MiddleFieldAdapter.ViewHolder>() {
    private var itemList = mutableListOf<MiddleFieldDto>()
    private var onItemClickListener: ((MiddleFieldDto, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListMiddlefieldBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListMiddlefieldBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: MiddleFieldDto) {
            binding.txtCategoryName.text = item.name
        }
    }

    fun setOnItemClickListener(listener: (MiddleFieldDto, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<MiddleFieldDto>) {
        val diffCallBack = MiddleFieldDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}