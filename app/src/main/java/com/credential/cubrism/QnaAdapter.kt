package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// InfoClickListener 인터페이스 정의
interface QnaClickListener {
    fun onqnaClick(item: String)
}


class QnaAdapter(private val items: ArrayList<String>) : RecyclerView.Adapter<QnaAdapter.QnaViewHolder>() {
    private var qnaClickListener: QnaClickListener? = null
    fun setQnaClickListener(listener: QnaClickListener) {
        qnaClickListener = listener
    }
    inner class QnaViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val qnane = v.findViewById<TextView>(R.id.qnaListView)
        init {
            v.setOnClickListener {
                val position = adapterPosition
                val clickedInfo = items[position]
                qnaClickListener?.onqnaClick(clickedInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QnaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_photo_qna, parent, false)
        return QnaViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: QnaViewHolder, position: Int) {
        holder.qnane.text = items[position]
    }
}