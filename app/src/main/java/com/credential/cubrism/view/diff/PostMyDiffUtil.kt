package com.credential.cubrism.view.diff

import androidx.recyclerview.widget.DiffUtil
import com.credential.cubrism.model.dto.PostMyList

class PostMyDiffUtil(private val oldList: List<PostMyList>, private val newList: List<PostMyList>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].postId == newList[newItemPosition].postId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}