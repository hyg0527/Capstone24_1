package com.credential.cubrism.view.diff

import androidx.recyclerview.widget.DiffUtil
import com.credential.cubrism.model.dto.ScheduleListDto

class ScheduleDiffUtil(private val oldList: List<ScheduleListDto>, private val newList: List<ScheduleListDto>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].scheduleId == newList[newItemPosition].scheduleId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}