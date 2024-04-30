package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.databinding.ItemListMenuBinding
import com.credential.cubrism.model.dto.MenuDto
import com.credential.cubrism.view.diff.MenuDiffUtil

class MenuAdapter : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    private var itemList = mutableListOf<MenuDto>()
    private var onItemClickListener: ((MenuDto, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListMenuBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: MenuDto) {
            Glide.with(binding.root).load(item.icon).into(binding.imgMenu)
            binding.txtMenu.text = item.text
        }
    }

    fun setOnItemClickListener(listener: (MenuDto, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<MenuDto>) {
        val diffCallBack = MenuDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}