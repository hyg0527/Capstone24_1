package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListCommentMyBinding
import com.credential.cubrism.databinding.ItemListCommentOthersBinding
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
        fun bind(item: ChatResponseDto) {
            binding.txtMessage.text = item.content.replace(" ", "\u00A0")
            binding.txtTime.text = convertDateTimeFormat(item.createdAt, "yyyy-MM-dd'T'HH:mm:ss.[SSSSSS][SSSSS][SSSS][SSS][SS][S]", "M.d HH:mm:ss") // 임시 형식
        }
    }

    inner class OthersViewHolder(private val binding: ItemListCommentOthersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatResponseDto) {
            Glide.with(binding.root).load(item.profileImgUrl)
                .error(R.drawable.profil_image)
                .fallback(R.drawable.profil_image)
                .into(binding.imgProfile)
            binding.txtNickname.text = item.username ?: "(알수없음)"
            binding.txtMessage.text = item.content.replace(" ", "\u00A0")
            binding.txtTime.text = convertDateTimeFormat(item.createdAt, "yyyy-MM-dd'T'HH:mm:ss.[SSSSSS][SSSSS][SSSS][SSS][SS][S]", "M.d HH:mm:ss") // 임시 형식
            binding.imgReplyTo.visibility = View.GONE
        }
    }

    fun setItemList(list: List<ChatResponseDto>) {
        val diffCallback = ChatDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addItem(item: ChatResponseDto) {
        itemList.add(item)
        notifyItemInserted(itemList.size - 1)
    }
}