package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

interface ReplyListener {
    fun onClicked(isReply: Boolean)
}
data class Chat(val userName: String?= null, val profileImg: Int? = null,
                val text: String? = null, val reply: Boolean = false, val showReplyImg: Boolean = false)

class ChattingAdapter(private val items: ArrayList<Chat>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: ReplyListener? = null
    private var isReply = false
    fun setReplyListener(listener: ReplyListener) {
        this.listener = listener
    }

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val chat = v.findViewById<TextView>(R.id.tv_chat_text)
        val myreplyimg = v.findViewById<ImageView>(R.id.my_replyIMG)
        val myreplynick = v.findViewById<TextView>(R.id.other_replyUser_ofmine)
    }
    inner class YourViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val chat = v.findViewById<TextView>(R.id.tv_chat_text)
        val yourreplybtn = v.findViewById<ImageView>(R.id.other_replybtn)
        val yourreplyimg = v.findViewById<ImageView>(R.id.other_replyIMG)
        val yourreplynick = v.findViewById<TextView>(R.id.other_replyUser_ofany)
    }

    companion object {
        private const val MY_CHAT = 1
        private const val OTHER_CHAT = 2
    }

    override fun getItemViewType(position: Int): Int { // 현재 유저 정보를 받아와서 유저정보가 현재 유저와 일치하면 오른쪽에 메시지를 띄우고, 아니면 오른쪽에 메시지 띄우기
        return if(items[position].text?.take(1).equals("-")) OTHER_CHAT
        else MY_CHAT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {    // 왼쪽 오른쪽 각각의 레이아웃 inflate
            MY_CHAT -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.text_layout, parent, false)
                MyViewHolder(itemView)
            }
            OTHER_CHAT -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.text_layout_receive, parent, false)
                YourViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type") // 예외 처리
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {     // 각각의 레이아웃에서 정보를 textview에 출력
            is MyViewHolder -> { // 뷰 타입 A에 대한 데이터 바인딩
                holder.chat.text = items[position].text
                holder.myreplyimg.setImageResource(items[position].profileImg ?: 0)
                holder.myreplynick.text = items[position].userName

                val visibility = if (items[position].showReplyImg) View.VISIBLE else View.GONE
                holder.myreplyimg.visibility = visibility
                holder.myreplynick.visibility = visibility

            }
            is YourViewHolder -> { // 뷰 타입 B에 대한 데이터 바인딩
                holder.chat.text = items[position].text
                holder.yourreplyimg.setImageResource(items[position].profileImg ?: 0)
                holder.yourreplynick.text = items[position].userName
                holder.yourreplybtn.setOnClickListener {
                    listener?.onClicked(isReply)
                    isReply = !isReply
                }

                val visibility = if (items[position].showReplyImg) View.VISIBLE else View.GONE
                holder.yourreplyimg.visibility = visibility
                holder.yourreplynick.visibility = visibility
            }
        }
    }

    fun addItem(item: Chat) {
        items.add(item)
        notifyDataSetChanged()
    }

    fun getItem(): ArrayList<Chat> {
        return items
    }
}