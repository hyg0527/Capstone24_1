package com.credential.cubrism

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

interface StudyItemClickListener {
    fun onItemClicked(item: StudyList)
}
data class StudyList(val title: String? = null, val info: String? = null,
                     val tagList: ArrayList<Tags>, val totalNum: Int? = null, val num: Int? = null): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        ArrayList<Tags>().apply {
            parcel.readList(this, Tags::class.java.classLoader)
        },
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(info)
        parcel.writeValue(totalNum)
        parcel.writeValue(num)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StudyList> {
        override fun createFromParcel(parcel: Parcel): StudyList {
            return StudyList(parcel)
        }

        override fun newArray(size: Int): Array<StudyList?> {
            return arrayOfNulls(size)
        }
    }
}

class StudyListAdapter(private val items: ArrayList<StudyList>,
                       val isMyStudy: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemClickListener: StudyItemClickListener? = null
    fun setItemClickListener(listener: StudyItemClickListener) {
        this.itemClickListener = listener
    }

    inner class StudyViewHolder(v: View) : RecyclerView.ViewHolder(v) { // 두번째탭의 리스트 화면에 보여질 viewholder
        val title = v.findViewById<TextView>(R.id.txtStudyTitle)
        val num = v.findViewById<TextView>(R.id.txtNumsStudy)
        val info = v.findViewById<TextView>(R.id.txtStudyInfo)
        val recyclerView = v.findViewById<RecyclerView>(R.id.tagRecyclerViewList)
        val recruiting = v.findViewById<Button>(R.id.studyStatus_ing)
        val recruitComplete = v.findViewById<Button>(R.id.studyStatus_end)

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
                val context = holder.itemView.context
                val item = items[position]
                val numText = "${item.num} / ${item.totalNum}"

                holder.title.text = item.title
                holder.num.text = numText
                holder.info.text = item.info

                holder.recyclerView.adapter = TagAdapter(item.tagList, false)
                holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                isRecruitCompleted(item, holder)
            }
            is MyStudyViewHolder -> {
                holder.studyTitle.text = items[position].title
            }
        }
    }

    private fun isRecruitCompleted(item: StudyList, holder: StudyViewHolder) {
        if ((item.num ?: 0) >= (item.totalNum ?: 0)) {
            holder.recruiting.visibility = View.GONE
            holder.recruitComplete.visibility = View.VISIBLE
        } else {
            holder.recruiting.visibility = View.VISIBLE
            holder.recruitComplete.visibility = View.GONE
        }
    }

    fun filterItem() { // 모집중/모집완료 필터링 함수
        val filteredItem = ArrayList<StudyList>()

        for (item in items) {
            if ((item.num ?: 0) < (item.totalNum ?: 0))
                filteredItem.add(item)
        }

        clearItem()
        items.addAll(filteredItem)
        notifyDataSetChanged()
    }

    fun addAllItems(item: ArrayList<StudyList>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
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