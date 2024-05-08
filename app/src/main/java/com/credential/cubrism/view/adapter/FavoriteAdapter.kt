package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListMypageCertBinding
import com.credential.cubrism.model.dto.FavoriteListDto
import com.credential.cubrism.view.diff.FavoriteDiffUtil

interface FavoriteDeleteButtonClickListener {
    fun onButtonClick(item: FavoriteListDto)
}

class FavoriteAdapter(private val listener: FavoriteDeleteButtonClickListener) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    private var itemList = mutableListOf<FavoriteListDto>()

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListMypageCertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListMypageCertBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavoriteListDto) {
            binding.txtCount.text = item.index.toString()
            binding.txtName.text = item.name

            binding.btnDelete.setOnClickListener {
                listener.onButtonClick(item)
            }
        }
    }

    fun setItemList(list: List<FavoriteListDto>) {
        val diffCallback = FavoriteDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}