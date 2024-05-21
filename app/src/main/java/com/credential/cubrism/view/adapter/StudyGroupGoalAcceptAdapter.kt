package com.credential.cubrism.view.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListGoalManageBinding
import com.credential.cubrism.model.dto.StudyGroupGoalSubmitListDto
import com.credential.cubrism.view.diff.StudyGroupGoalSubmitDiffUtil
import com.credential.cubrism.view.utils.ConvertDateTimeFormat
import java.util.Locale

interface GroupSubmitAcceptClickListener {
    fun onAcceptClick(item: StudyGroupGoalSubmitListDto)
}

interface GroupSubmitDenyClickListener {
    fun onDenyClick(item: StudyGroupGoalSubmitListDto)
}

class StudyGroupGoalAcceptAdapter(private val listenerAccept: GroupSubmitAcceptClickListener, private val listenerDeny: GroupSubmitDenyClickListener) : RecyclerView.Adapter<StudyGroupGoalAcceptAdapter.ViewHolder>() {
    private var itemList = mutableListOf<StudyGroupGoalSubmitListDto>()

    override fun getItemCount(): Int = itemList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListGoalManageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListGoalManageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyGroupGoalSubmitListDto) {
            binding.progressIndicator.show()

            Glide.with(binding.root).load(item.profileImageUrl)
                .error(R.drawable.profile)
                .fallback(R.drawable.profile)
                .dontAnimate()
                .into(binding.imgProfile)

            Glide.with(binding.root)
                .load(item.imageUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        binding.progressIndicator.hide()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        binding.progressIndicator.hide()
                        return false
                    }
                })
                .into(binding.imgPhoto)

            binding.txtNickname.text = item.nickname
            binding.txtDate.text = ConvertDateTimeFormat.convertDateTimeFormat(item.submittedAt, "yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd", Locale.KOREA)
            binding.txtGoal.text = item.goalName
            binding.txtContent.text = item.content

            binding.btnAccept.setOnClickListener {
                listenerAccept.onAcceptClick(item)
            }

            binding.btnDeny.setOnClickListener {
                listenerDeny.onDenyClick(item)
            }
        }
    }

    fun setItemList(list: List<StudyGroupGoalSubmitListDto>) {
        val diffCallback = StudyGroupGoalSubmitDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}