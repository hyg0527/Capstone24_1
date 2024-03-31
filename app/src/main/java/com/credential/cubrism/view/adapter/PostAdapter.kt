package com.credential.cubrism.view.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.credential.cubrism.databinding.ItemListProgresBinding
import com.credential.cubrism.databinding.ItemListQnaBinding
import com.credential.cubrism.model.dto.PostList
import com.credential.cubrism.view.diff.PostDiffUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class PostAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            val binding = ItemListQnaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(private val binding: ItemListQnaBinding) : RecyclerView.ViewHolder(binding.root) {
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
                val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(40))
                Glide.with(binding.root).load(item.imageUrl).apply(requestOptions).placeholder(ColorDrawable(Color.TRANSPARENT)).transition(DrawableTransitionOptions.withCrossFade()).into(binding.imgThumbnail)
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
            binding.txtDate.text = getTimeAgo(item.createdDate)
        }
    }

    inner class LoadingViewHolder(binding: ItemListProgresBinding) : RecyclerView.ViewHolder(binding.root) {
        val progressIndicator: View = binding.progressIndicator
    }

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

    private fun getTimeAgo(dateString: String?): String {
        dateString?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date: Date = dateFormat.parse(it) ?: return ""

            // 현재 날짜와 게시글 날짜의 차이 계산
            val diff = Date().time - date.time

            // 시간 단위로 변환
            val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val days = TimeUnit.MILLISECONDS.toDays(diff)

            val dateFormatter = SimpleDateFormat("M/dd", Locale.getDefault())

            return when {
                seconds < 60 -> "방금 전" // 60초 이내
                minutes < 60 -> "${minutes}분 전" // 60분 이내
                hours < 24 -> "${hours}시간 전" // 하루 이내
                days < 7 -> "${days}일 전" // 1주일 이내
                else -> dateFormatter.format(date) // 그 외
            }
        }

        // 날짜가 null일 경우 빈 문자열 반환
        return ""
    }
}