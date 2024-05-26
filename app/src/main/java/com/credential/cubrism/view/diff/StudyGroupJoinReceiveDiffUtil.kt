package com.credential.cubrism.view.diff

import androidx.recyclerview.widget.DiffUtil
import com.credential.cubrism.model.dto.StudyGroupJoinReceiveListDto

class StudyGroupJoinReceiveDiffUtil(private val oldList: List<StudyGroupJoinReceiveListDto>, private val newList: List<StudyGroupJoinReceiveListDto>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].memberId == newList[newItemPosition].memberId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}