package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListJoinacceptBinding
import com.credential.cubrism.model.dto.StudyGroupJoinReceiveListDto
import com.credential.cubrism.view.diff.StudyGroupJoinReceiveDiffUtil

class JoinAcceptAdapter(private val listener: OnViewClickListener) : RecyclerView.Adapter<JoinAcceptAdapter.ViewHolder>() {
    interface OnViewClickListener {
        fun setOnViewClick(viewId: Int, item: StudyGroupJoinReceiveListDto)
    }

    private var itemList = mutableListOf<StudyGroupJoinReceiveListDto>()

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListJoinacceptBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListJoinacceptBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyGroupJoinReceiveListDto) {
            Glide.with(binding.root)
                .load(item.userImage)
                .error(R.drawable.profile)
                .fallback(R.drawable.profile)
                .dontAnimate()
                .into(binding.imgProfile)

            binding.txtName.text = item.userName

            binding.btnAccept.setOnClickListener {
                listener.setOnViewClick(it.id, item)
            }

            binding.btnDeny.setOnClickListener {
                listener.setOnViewClick(it.id, item)
            }
        }
    }

    fun setItemList(list: List<StudyGroupJoinReceiveListDto>) {
        val diffCallback = StudyGroupJoinReceiveDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}