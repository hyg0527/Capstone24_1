package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListMypostBinding
import com.credential.cubrism.databinding.ItemListProgresBinding
import com.credential.cubrism.model.dto.PostMyList
import com.credential.cubrism.view.diff.PostMyDiffUtil
import com.credential.cubrism.view.utils.ItemDecoratorDivider

class PostMyAdapter(private val listener: OnViewClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnViewClickListener {
        fun setOnViewClick(item: PostMyList)
    }

    private var itemList = mutableListOf<PostMyList>()
    private var onItemClickListener: ((PostMyList, Int) -> Unit)? = null

    private var isLoading = false

    private val viewPool = RecyclerView.RecycledViewPool()

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    override fun getItemCount(): Int = if (isLoading) itemList.size + 1 else itemList.size

    override fun getItemViewType(position: Int): Int = if (isLoading && position == itemList.size) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemListMypostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        } else {
            val binding = ItemListProgresBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LoadingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(itemList[position])
        } else if (holder is LoadingViewHolder) {
            holder.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    inner class ViewHolder(private val binding: ItemListMypostBinding) : RecyclerView.ViewHolder(binding.root) {
        private val postImageAdapter = PostImageAdapter()

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }

            binding.recyclerImage.apply {
                adapter = postImageAdapter
                setHasFixedSize(true)
                setRecycledViewPool(viewPool)
                addItemDecoration(ItemDecoratorDivider(context, 0, 0, 0, 12, 0, 0, null))
            }
        }

        fun bind(item: PostMyList) {
            Glide.with(binding.root).apply {
                load(item.profileImage)
                    .error(R.drawable.profile)
                    .fallback(R.drawable.profile)
                    .dontAnimate()
                    .into(binding.imgProfile)

                load(R.drawable.icon_comment).into(binding.imgComment)
                load(R.drawable.menu_dot).into(binding.imgMenu)
            }

            binding.txtCategory.text = item.category
            binding.txtNickname.text = item.nickname
            binding.txtTitle.text = item.title
            binding.txtContent.text = item.content.replace(" ", "\u00A0")
            binding.txtDate.text = item.createdDate
            binding.txtCommentCount.text = item.commentCount.toString()
            postImageAdapter.setItemList(item.images)

            binding.imgMenu.setOnClickListener {
                listener.setOnViewClick(item)
            }
        }
    }

    inner class LoadingViewHolder(binding: ItemListProgresBinding) : RecyclerView.ViewHolder(binding.root) {
        val progressIndicator: View = binding.progressIndicator
    }

    fun setOnItemClickListener(listener: (PostMyList, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<PostMyList>) {
        val diffCallback = PostMyDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemList(): List<PostMyList> {
        return itemList
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