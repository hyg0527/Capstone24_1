package com.credential.cubrism.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListCommentMyBinding
import com.credential.cubrism.databinding.ItemListCommentOthersBinding
import com.credential.cubrism.model.dto.Comments
import com.credential.cubrism.view.CommentDialog
import com.credential.cubrism.view.diff.PostCommentDiffUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface OnReplyClickListener {
    fun onReplyClick(nickname: String, commentId: Int)
}

class PostCommentAdapter(private val context: Context, private val myEmail: String?, private val listener: OnReplyClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            binding.txtMessage.text = item.content.replace(" ", "\u00A0")
            binding.txtTime.text = convertDate(item.createdDate)
            binding.txtUpdated.visibility = if (item.isUpdated) View.VISIBLE else View.GONE
            binding.txtReply.visibility = if (item.isReply) View.VISIBLE else View.GONE

            binding.layout.setOnLongClickListener {
                CommentDialog(item).show((context as AppCompatActivity).supportFragmentManager, "comment")
                true
            }
        }
    }

    inner class OthersViewHolder(private val binding: ItemListCommentOthersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comments) {
            Glide.with(binding.root).load(item.profileImageUrl)
                .error(R.drawable.profil_image)
                .fallback(R.drawable.profil_image)
                .into(binding.imgProfile)
            binding.txtNickname.text = item.nickname
            binding.txtMessage.text = item.content.replace(" ", "\u00A0")
            binding.txtTime.text = convertDate(item.createdDate)
            binding.txtUpdated.visibility = if (item.isUpdated) View.VISIBLE else View.GONE
            binding.txtReply.visibility = if (item.isReply) View.VISIBLE else View.GONE

            binding.imgReplyTo.setOnClickListener {
                listener.onReplyClick(item.nickname, item.commentId)
            }
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