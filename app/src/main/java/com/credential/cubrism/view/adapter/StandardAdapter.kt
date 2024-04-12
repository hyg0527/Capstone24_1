package com.credential.cubrism.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListFileBinding
import com.credential.cubrism.model.dto.Standard
import com.credential.cubrism.view.diff.StandardDiffUtil

class StandardAdapter : RecyclerView.Adapter<StandardAdapter.ViewHolder>() {
    private var itemList = mutableListOf<Standard>()
    private var onItemClickListener: ((Standard, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListFileBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListFileBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: Standard) {
            when (item.fileName.substringAfterLast('.')) {
                "pdf" -> Glide.with(binding.root).load(R.drawable.icon_pdf).into(binding.imgFile)
                "hwp" -> Glide.with(binding.root).load(R.drawable.icon_hwp).into(binding.imgFile)
                "zip" -> Glide.with(binding.root).load(R.drawable.icon_zip).into(binding.imgFile)
                else -> Glide.with(binding.root).load(R.drawable.icon_etc).into(binding.imgFile)
            }
            binding.txtFile.text = item.fileName.replace(" ", "\u00A0")
        }
    }

    fun setOnItemClickListener(listener: (Standard, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<Standard>) {
        Log.d("테스트", "[StandardAdapter] $list")

        val diffCallBack = StandardDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}