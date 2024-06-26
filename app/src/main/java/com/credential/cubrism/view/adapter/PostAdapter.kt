package com.credential.cubrism.view.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.credential.cubrism.databinding.ItemListPostBinding
import com.credential.cubrism.databinding.ItemListProgresBinding
import com.credential.cubrism.model.dto.PostList
import com.credential.cubrism.view.diff.PostDiffUtil

class PostAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = mutableListOf<PostList>()
    private var onItemClickListener: ((PostList, Int) -> Unit)? = null

    private var isLoading = false

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    override fun getItemCount(): Int = if (isLoading) itemList.size + 1 else itemList.size

    override fun getItemViewType(position: Int): Int = if (isLoading && position == itemList.size) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemListPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        } else {
            val binding = ItemListProgresBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LoadingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(itemList[position])
        }
    }

    inner class ViewHolder(private val binding: ItemListPostBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: PostList) {
            if (item.imageUrl.isNullOrEmpty()) {
                binding.imgThumbnail.visibility = View.GONE
            } else {
                binding.imgThumbnail.visibility = View.VISIBLE
                Glide.with(binding.root).load(item.imageUrl)
                    .placeholder(ColorDrawable(Color.TRANSPARENT))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .sizeMultiplier(0.5f)
                    .centerCrop()
                    .into(binding.imgThumbnail)
            }

            if (item.commentCount == 0) {
                binding.txtCommentCount.visibility = View.GONE
            } else {
                binding.txtCommentCount.visibility = View.VISIBLE
                binding.txtCommentCount.text = item.commentCount.toString()
            }

            binding.txtCategory.text = item.category
            binding.txtTitle.text = item.title
            binding.txtContent.text = item.content.replace(" ", "\u00A0")
            binding.txtDate.text = item.createdDate
        }
    }

    inner class LoadingViewHolder(binding: ItemListProgresBinding) : RecyclerView.ViewHolder(binding.root)

    fun setOnItemClickListener(listener: (PostList, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<PostList>) {
        val diffCallback = PostDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setLoading(isLoading: Boolean) {
        if (this.isLoading == isLoading)
            return

        val lastPosition = if (this.isLoading) itemList.size else itemList.size - 1
        this.isLoading = isLoading

        if (isLoading) {
            notifyItemInserted(itemList.size)
        } else {
            notifyItemRemoved(lastPosition)
        }
    }
}