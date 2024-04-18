package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.databinding.ItemListPostPhotoBinding
import com.credential.cubrism.view.diff.StringListDiffUtil

interface OnDeleteClickListener {
    fun onDeleteClick(position: Int)
}

class PostPhotoAdapter(private val listener: OnDeleteClickListener) : RecyclerView.Adapter<PostPhotoAdapter.ViewHolder>() {
    private var itemList = mutableListOf<String>()
    private var onItemClickListener: ((String, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListPostPhotoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListPostPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.imgPhoto.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: String) {
            Glide.with(binding.root).load(item).into(binding.imgPhoto)

            binding.btnDelete.setOnClickListener {
                listener.onDeleteClick(adapterPosition)
            }
        }
    }

    fun setOnItemClickListener(listener: (String, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<String>) {
        val diffCallBack = StringListDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemList(): List<String> {
        return itemList
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }
}
