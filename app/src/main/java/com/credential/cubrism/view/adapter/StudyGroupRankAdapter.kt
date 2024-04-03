package com.credential.cubrism.view.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R

data class Rank(val userName: String? = null, val percentage: Int? = null)

class StudyGroupRankAdapter(private val items: ArrayList<Rank>) : RecyclerView.Adapter<StudyGroupRankAdapter.RankViewHolder>() {
    inner class RankViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val userName = v.findViewById<TextView>(R.id.txtUserNameRank)
        val percentage = v.findViewById<ProgressBar>(R.id.progressBar)
        val perString = v.findViewById<TextView>(R.id.txtPercentageRank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_studygroup_rank, parent, false)

        return RankViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        val perStringTxt = items[position].percentage.toString() + "%"

        val finalProgress = (items[position].percentage ?: 0).toInt()
        val animation = ObjectAnimator.ofInt(holder.percentage, "progress", 0, finalProgress) // 애니메이션 생성
        animation.duration = 1000 // 애니메이션 지속 시간 설정 (밀리초 단위)
        animation.start()        // 애니메이션 시작

        holder.userName.text = items[position].userName
        holder.percentage.progress = finalProgress
        holder.perString.text = perStringTxt

    }

    fun reloadItems() { // 새로고침 함수
        notifyDataSetChanged()
    }
}