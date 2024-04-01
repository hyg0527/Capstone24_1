package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.databinding.ItemListCommentMyBinding
import com.credential.cubrism.databinding.ItemListCommentOthersBinding
import com.credential.cubrism.model.dto.Comments
import com.credential.cubrism.view.diff.PostCommentDiffUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostCommentAdapter(private val myEmail: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = mutableListOf<Comments>()

    companion object {
        private const val VIEW_TYPE_MY = 0
        private const val VIEW_TYPE_OTHERS = 1
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int = if (itemList[position].email == myEmail) VIEW_TYPE_MY else VIEW_TYPE_OTHERS

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MY) {
            val binding = ItemListCommentMyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyViewHolder(binding)
        } else {
            val binding = ItemListCommentOthersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            OthersViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bind(itemList[position])
        } else if (holder is OthersViewHolder) {
            holder.bind(itemList[position])
        }
    }

    inner class MyViewHolder(private val binding: ItemListCommentMyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comments) {
            binding.txtMessage.text = item.content
            binding.txtTime.text = convertDate(item.createdDate)
        }
    }

    inner class OthersViewHolder(private val binding: ItemListCommentOthersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comments) {
            Glide.with(binding.root).load(item.profileImageUrl).into(binding.imgProfile)
            binding.txtNickname.text = item.nickname
            binding.txtMessage.text = item.content
            binding.txtTime.text = convertDate(item.createdDate)
        }
    }

    fun setItemList(list: List<Comments>) {
        val diffCallback = PostCommentDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun convertDate(dateString: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val outputFormatTime = DateTimeFormatter.ofPattern("HH:mm")
        val outputFormatDate = DateTimeFormatter.ofPattern("M/d")

        val date = LocalDateTime.parse(dateString, inputFormat)

        val isToday = LocalDateTime.now().toLocalDate() == date.toLocalDate()

        return if (isToday) date.format(outputFormatTime) else date.format(outputFormatDate)
    }
}