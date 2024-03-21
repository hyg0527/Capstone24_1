package com.credential.cubrism

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface StudyItemClickListener {
    fun onItemClicked(item: StudyList)
}
data class StudyList(val title: String? = null, val info: String? = null, val num: Int? = null)

class StudyListAdapter(private val items: ArrayList<StudyList>,
                       val isMyStudy: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemClickListener: StudyItemClickListener? = null
    fun setItemClickListener(listener: StudyItemClickListener) {
        this.itemClickListener = listener
    }

    inner class StudyViewHolder(v: View) : RecyclerView.ViewHolder(v) { // 두번째탭의 리스트 화면에 보여질 viewholder
        val title = v.findViewById<TextView>(R.id.txtStudyTitle)
//        val num = v.findViewById<TextView>(R.id.txtNums)
        val info = v.findViewById<TextView>(R.id.txtStudyInfo)

        init {
            v.setOnClickListener {
                val position = adapterPosition
                val item = items[position]
                itemClickListener?.onItemClicked(item)
            }
        }
    }
    inner class MyStudyViewHolder(v: View) : RecyclerView.ViewHolder(v) { // 마이페이지의 내 그룹 리스트 화면에 보여질 viewholder
        val studyTitle = v.findViewById<TextView>(R.id.txtStudyGroupTitle)
        val enterBtn = v.findViewById<Button>(R.id.btnEnterStudy)

        init {
            enterBtn.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, StudyActivity::class.java)
                intent.putExtra("studyGroupTitle", studyTitle.text.toString())

                context.startActivity(intent)
            }
        }
    }

    companion object {
        private const val STUDY_LIST = 1
        private const val MY_STUDY = 2
    }

    override fun getItemViewType(position: Int): Int { // 현재 유저 정보를 받아와서 유저정보가 현재 유저와 일치하면 오른쪽에 메시지를 띄우고, 아니면 오른쪽에 메시지 띄우기
        return if(!isMyStudy) STUDY_LIST
        else MY_STUDY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            STUDY_LIST -> {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.item_list_study, parent, false)

                StudyViewHolder(view)
            }
            MY_STUDY -> {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.item_list_mystudy, parent, false)

                MyStudyViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type") // 예외 처리
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StudyViewHolder -> {
                holder.title.text = items[position].title
                holder.info.text = items[position].info
            }
            is MyStudyViewHolder -> {
                holder.studyTitle.text = items[position].title
            }
        }
    }

    fun addItem(item: StudyList) {
        items.add(item)
    }

    fun clearItem() {
        items.clear()
    }

    fun updateItem() {
        notifyDataSetChanged()
    }
}