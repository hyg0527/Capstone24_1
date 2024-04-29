package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListStudyJoinBinding
import com.credential.cubrism.model.dto.StudyGroupJoinListDto
import com.credential.cubrism.view.diff.StudyGroupJoinDiffUtil
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StudyGroupJoinAdapter : RecyclerView.Adapter<StudyGroupJoinAdapter.ViewHolder>() {
    private var itemList = mutableListOf<StudyGroupJoinListDto>()

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListStudyJoinBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListStudyJoinBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.recyclerView.addItemDecoration(ItemDecoratorDivider(0, 20, 0, 20, 0, 0, 0))
        }

        fun bind(item: StudyGroupJoinListDto) {
            binding.txtGruopName.text = item.groupName
            binding.txtGroupDescription.text = item.groupDescription
            val dateTime = LocalDateTime.parse(item.requestDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            binding.txtDate.text = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

            val tagAdapter = StudyGroupTagAdapter(1)
            binding.recyclerView.apply {
                adapter = tagAdapter
                setRecycledViewPool(viewPool)
                tagAdapter.setItemList(item.tags)
            }
        }
    }

    fun setItemList(list: List<StudyGroupJoinListDto>) {
        val diffCallback = StudyGroupJoinDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}