package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface QnaClickListener {
    fun onQnaClick(item: String)
}

data class QnaData(val title: String? = null, val postimg: Int? = null,
                   val postin: String? = null, val writingtime: String? = null)

class QnaAdapter(private val items: ArrayList<QnaData>) : RecyclerView.Adapter<QnaAdapter.QnaViewHolder>() {
    private var qnaClickListener: QnaClickListener? = null
    fun setQnaClickListener(listener: QnaClickListener) {
        qnaClickListener = listener
    }
    inner class QnaViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val qnane = v.findViewById<TextView>(R.id.textView15)
        init {
            v.setOnClickListener {
                val position = adapterPosition
                val clickedInfo = items[position]
                //qnaClickListener?.onqnaClick(clickedInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QnaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_list_qna, parent, false)
        return QnaViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: QnaViewHolder, position: Int) {
        holder.qnane.text = items[position].title
    }
}