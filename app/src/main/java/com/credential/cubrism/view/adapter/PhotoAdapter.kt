package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.databinding.PagePhotoBinding
import com.credential.cubrism.view.diff.StringListDiffUtil

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    private var itemList = mutableListOf<String>()

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PagePhotoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: PagePhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            Glide.with(binding.root)
                .load(item)
                .into(binding.imgPhoto)
        }
    }

    fun setItemList(list: List<String>) {
        val diffCallBack = StringListDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}