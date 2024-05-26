package com.credential.cubrism.view.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.credential.cubrism.databinding.ItemListMajorfieldBinding
import com.credential.cubrism.model.dto.MajorFieldDto
import com.credential.cubrism.view.diff.MajorFieldDiffUtil

class MajorFieldAdapter : RecyclerView.Adapter<MajorFieldAdapter.ViewHolder>() {
    private var itemList = mutableListOf<MajorFieldDto>()
    private var onItemClickListener: ((MajorFieldDto, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListMajorfieldBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListMajorfieldBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: MajorFieldDto) {
            Glide.with(binding.root).load(item.iconUrl).placeholder(ColorDrawable(Color.TRANSPARENT)).transition(DrawableTransitionOptions.withCrossFade()).into(binding.icon)
            binding.txtMajorField.text = item.majorFieldName
        }
    }

    fun setOnItemClickListener(listener: (MajorFieldDto, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<MajorFieldDto>) {
        val diffCallBack = MajorFieldDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}