package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


interface QnaBannerEnterListener {
    fun onBannerClicked()
    fun onBannerStudyClicked()
}
data class BannerData(val bannerImg: Int? = null, val bannerTxt: String? = null)

class BannerAdapter : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    private var listener: QnaBannerEnterListener? = null
    fun setBannerListener(listener: QnaBannerEnterListener) {
        this.listener = listener
    }

    inner class BannerViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val bannerImg = v.findViewById<ImageView>(R.id.imageView8)
        val bannerTxt = v.findViewById<TextView>(R.id.textView40)
        val bannerbtn = v.findViewById<Button>(R.id.quitBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_list_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun getItemCount(): Int { // 무한 스크롤 되도록 최댓값으로 설정
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val context = holder.itemView.context
        // position이 짝수면 첫 번째 아이템, 홀수면 두 번째 아이템
        if (position % 2 == 0) {
            holder.bannerTxt.text = "궁금한 것이 있을 땐?\nQ&A 게시판에 질문하세요!"
            holder.bannerbtn.setOnClickListener {
                listener?.onBannerClicked()
            }
        } else {
            holder.bannerTxt.text = "같이 공부해요!\n스터디그룹을 결성해보세요!"
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
            holder.bannerbtn.setOnClickListener {
                listener?.onBannerStudyClicked()
            }
        }
        holder.itemView.invalidate()
    }
}