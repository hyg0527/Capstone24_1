package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListBannerBinding

enum class BannerType {
    QNA, STUDYGROUP
}

interface BannerEnterListener {
    fun onBannerClicked(type: BannerType)
}

class BannerAdapter(private val listener: BannerEnterListener) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    override fun getItemCount(): Int = Int.MAX_VALUE // 무한 스크롤 되도록 최댓값으로 설정

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ItemListBannerBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(private val binding: ItemListBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val context = itemView.context
            // position이 짝수면 첫 번째 아이템, 홀수면 두 번째 아이템
            if (adapterPosition % 2 == 0) { // qna
                binding.icon.setImageResource(R.drawable.qnaicon)
                binding.txtBanner.text = "궁금한 것이 있을 땐?\nQ&A 게시판에 질문하세요!"
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.bannerYellow))
                binding.btnGo.apply{
                    setBackgroundResource(R.drawable.button_rounded_corner_red)
                    setOnClickListener {
                        listener.onBannerClicked(BannerType.QNA)
                    }
                }
            } else { // studygroup
                binding.icon.setImageResource(R.drawable.studybannericon)
                binding.txtBanner.text = "같이 공부해요!\n스터디그룹을 결성해보세요!"
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.bannerPurple))
                binding.btnGo.apply{
                    setBackgroundResource(R.drawable.button_rounded_corner_bannerpurple)
                    setOnClickListener {
                        listener.onBannerClicked(BannerType.STUDYGROUP)
                    }
                }
            }
        }
    }
}