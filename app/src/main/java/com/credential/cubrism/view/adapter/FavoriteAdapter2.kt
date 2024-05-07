package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListMylicenselistBinding
import com.credential.cubrism.model.dto.FavoriteListDto
import com.credential.cubrism.view.diff.FavoriteDiffUtil

class FavoriteAdapter2 : RecyclerView.Adapter<FavoriteAdapter2.ViewHolder>() {
    private var itemList = mutableListOf<FavoriteListDto>()

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListMylicenselistBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListMylicenselistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavoriteListDto) {
            Glide.with(binding.root).run {
                if ((adapterPosition + 1) % 3 == 1) {
                    load(R.drawable.red_pin)
                } else if ((adapterPosition + 1) % 3 == 2) {
                    load(R.drawable.yellow_pin)
                } else {
                    load(R.drawable.green_pin)
                }
            }.into(binding.imgPin)

            binding.view.background = if ((adapterPosition + 1) % 2 == 0) {
                ResourcesCompat.getDrawable(binding.root.resources, R.color.mdblue, null)
            } else {
                ResourcesCompat.getDrawable(binding.root.resources, R.color.blue, null)
            }

            binding.txtname.text = item.name
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