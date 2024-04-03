package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListMypageBinding
import com.credential.cubrism.model.dto.MyPageDto
import com.credential.cubrism.view.diff.MyPageDiffUtil

class MyPageAdapter : RecyclerView.Adapter<MyPageAdapter.ViewHolder>() {
    private var itemList = mutableListOf<MyPageDto>()
    private var onItemClickListener: ((MyPageDto, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListMypageBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListMypageBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: MyPageDto) {
            binding.txtTitle.text = item.title
        }
    }

    fun setOnItemClickListener(listener: (MyPageDto, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<MyPageDto>) {
        val diffCallBack = MyPageDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}