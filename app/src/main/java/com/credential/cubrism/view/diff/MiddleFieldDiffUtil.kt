package com.credential.cubrism.view.diff

import androidx.recyclerview.widget.DiffUtil
import com.credential.cubrism.model.dto.MiddleFieldDto

class MiddleFieldDiffUtil(private val oldList: List<MiddleFieldDto>, private val newList: List<MiddleFieldDto>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}