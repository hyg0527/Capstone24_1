package com.credential.cubrism.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemPhotoQnaBinding

class QnaPhotoAdapter(private val photoImg: ArrayList<Uri>) : RecyclerView.Adapter<QnaPhotoAdapter.QnaPhotoViewHolder>() {
    inner class QnaPhotoViewHolder(val binding: ItemPhotoQnaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Uri) {
            binding.qnaImage.setImageURI(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QnaPhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoQnaBinding.inflate(inflater, parent, false)

        return QnaPhotoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photoImg.count()
    }

    override fun onBindViewHolder(holder: QnaPhotoViewHolder, position: Int) {
        holder.bind(photoImg[position])
    }

    fun addItem(item: Uri) {
        photoImg.add(item)
        notifyDataSetChanged()
    }
}
