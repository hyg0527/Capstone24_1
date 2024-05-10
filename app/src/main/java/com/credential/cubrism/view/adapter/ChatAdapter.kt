package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListChatMyBinding
import com.credential.cubrism.databinding.ItemListChatOthersBinding
import com.credential.cubrism.model.dto.ChatResponseDto
import com.credential.cubrism.view.diff.ChatDiffUtil
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertDateTimeFormat

class ChatAdapter(private val myEmail: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = mutableListOf<ChatResponseDto>()

    companion object {
        private const val VIEW_TYPE_MY = 0
        private const val VIEW_TYPE_OTHERS = 1
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int = if (itemList[position].email == myEmail) VIEW_TYPE_MY else VIEW_TYPE_OTHERS

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MY) {
            val binding = ItemListChatMyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyViewHolder(binding)
        } else {
            val binding = ItemListChatOthersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class MyViewHolder(private val binding: ItemListChatMyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatResponseDto) {
            binding.txtMessage.text = item.content.replace(" ", "\u00A0")
            val formattedTime = item.createdAt.substringBeforeLast('.')
            binding.txtTime.text = convertDateTimeFormat(formattedTime, "yyyy-MM-dd'T'HH:mm:ss", "H:mm")
            binding.txtDate.apply {
                if (item.isDateHeader) {
                    visibility = View.VISIBLE
                    text = convertDateTimeFormat(formattedTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyy년 M월 d일 EEEE")
                } else {
                    visibility = View.GONE
                }
            }
        }
    }

    inner class OthersViewHolder(private val binding: ItemListChatOthersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatResponseDto) {
            Glide.with(binding.root).load(item.profileImgUrl)
                .error(R.drawable.profile)
                .fallback(R.drawable.profile)
                .dontAnimate()
                .into(binding.imgProfile)
            binding.txtNickname.text = item.username ?: "(알수없음)"
            binding.txtMessage.text = item.content.replace(" ", "\u00A0")
            val formattedTime = item.createdAt.substringBeforeLast('.')
            binding.txtTime.text = convertDateTimeFormat(formattedTime, "yyyy-MM-dd'T'HH:mm:ss", "H:mm")
            binding.txtDate.apply {
                if (item.isDateHeader) {
                    visibility = View.VISIBLE
                    text = convertDateTimeFormat(formattedTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyy년 M월 d일 EEEE")
                } else {
                    visibility = View.GONE
                }
            }
        }
    }

    fun setItemList(list: List<ChatResponseDto>) {
        val diffCallback = ChatDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}