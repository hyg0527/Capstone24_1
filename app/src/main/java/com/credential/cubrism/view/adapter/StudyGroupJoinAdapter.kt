package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListStudyJoinBinding
import com.credential.cubrism.model.dto.StudyGroupJoinListDto
import com.credential.cubrism.view.diff.StudyGroupJoinDiffUtil
import com.credential.cubrism.view.utils.ConvertDateTimeFormat.convertDateTimeFormat
import java.util.Locale

class StudyGroupJoinAdapter(private val listener: OnViewClickListener) : RecyclerView.Adapter<StudyGroupJoinAdapter.ViewHolder>() {
    interface OnViewClickListener {
        fun setOnViewClick(item: StudyGroupJoinListDto)
    }

    private var itemList = mutableListOf<StudyGroupJoinListDto>()

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
        fun bind(item: StudyGroupJoinListDto) {
            Glide.with(binding.root).apply {
                load(item.groupAdminProfileImage)
                    .error(R.drawable.profile)
                    .fallback(R.drawable.profile)
                    .into(binding.imgProfile)

                load(R.drawable.menu_dot).into(binding.imgMenu)
            }

            binding.txtNickname.text = item.groupAdmin
            binding.txtGroupName.text = item.groupName
            binding.txtDate.text = convertDateTimeFormat(item.requestDate, "yyyy-MM-dd'T'HH:mm:ss", "M/d HH:mm", Locale.KOREA)

            binding.imgMenu.setOnClickListener {
                listener.setOnViewClick(item)
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