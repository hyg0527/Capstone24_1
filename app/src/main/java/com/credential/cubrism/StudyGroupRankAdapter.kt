package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudyGroupRankAdapter(private val items: ArrayList<String>) : RecyclerView.Adapter<StudyGroupRankAdapter.RankViewHolder>() {
    inner class RankViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val userName = v.findViewById<TextView>(R.id.txtUserNameRank)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyGroupRankAdapter.RankViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_studygroup_rank, parent, false)

        return RankViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        holder.userName.text = items[position]
    }
}