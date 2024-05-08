package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListMylicenselistAddBinding
import com.credential.cubrism.databinding.ItemListMylicenselistBinding
import com.credential.cubrism.model.dto.FavoriteListDto
import com.credential.cubrism.view.diff.FavoriteItemDiffUtil

enum class FavType {
    FAVORITE, ADD
}

data class FavoriteItem(
    val type: FavType,
    val data: Any?
)

class FavoriteAdapter2 : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = mutableListOf<FavoriteItem>()
    private var onItemClickListener: ((Any, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].type) {
            FavType.FAVORITE -> 0
            FavType.ADD -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> FavoriteViewHolder(ItemListMylicenselistBinding.inflate(inflater, parent, false))
            else -> AddViewHolder(ItemListMylicenselistAddBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FavoriteViewHolder -> holder.bind(itemList[position].data as FavoriteListDto)
            is AddViewHolder -> holder.bind(itemList[position])
        }
    }

    inner class FavoriteViewHolder(private val binding: ItemListMylicenselistBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position].data as FavoriteListDto, position)
                }
            }
        }

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

    inner class AddViewHolder(private val binding: ItemListMylicenselistAddBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: FavoriteItem) {}
    }

    fun setOnItemClickListener(listener: (Any, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<FavoriteItem>) {
        val diffCallback = FavoriteItemDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}