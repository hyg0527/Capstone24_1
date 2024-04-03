package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R

class QnaPhotoAdapter(private val photoimg: ArrayList<Int>) : RecyclerView.Adapter<QnaPhotoAdapter.QnaPhotoViewHolder>() {
    inner class QnaPhotoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val photo = v.findViewById<ImageView>(R.id.qnaImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QnaPhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_photo_qna, parent, false)

        return QnaPhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photoimg.count()
    }

    override fun onBindViewHolder(holder: QnaPhotoViewHolder, position: Int) {
        holder.photo.setImageResource(photoimg[position])
    }

    fun addItem(item: Int) {
        photoimg.add(item)
        notifyDataSetChanged()
    }
}
