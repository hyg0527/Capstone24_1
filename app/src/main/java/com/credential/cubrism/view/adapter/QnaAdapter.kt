package com.credential.cubrism.view.adapter

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.view.QnaViewActivity

data class QnaData(val medalName: String? = null, val title: String? = null, val postImg: Int? = null,
                   val postIn: String? = null, val writingTime: String? = null, val userName: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) { }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(medalName)
        parcel.writeString(title)
        parcel.writeValue(postImg)
        parcel.writeString(postIn)
        parcel.writeString(writingTime)
        parcel.writeString(userName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QnaData> {
        override fun createFromParcel(parcel: Parcel): QnaData {
            return QnaData(parcel)
        }

        override fun newArray(size: Int): Array<QnaData?> {
            return arrayOfNulls(size)
        }
    }

}

class QnaAdapter(private val items: ArrayList<QnaData>) : RecyclerView.Adapter<QnaAdapter.QnaViewHolder>() {
    inner class QnaViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val medalName = v.findViewById<TextView>(R.id.txtCategory)
        val qName = v.findViewById<TextView>(R.id.txtTitle)
        val qInfo = v.findViewById<TextView>(R.id.txtContent)
        val time = v.findViewById<TextView>(R.id.txtDate)
        val thumbNail = v.findViewById<ImageView>(R.id.imgThumbnail)

        init {
            v.setOnClickListener {
                val position = adapterPosition
                val clickedInfo = items[position]

                val context = itemView.context
                val intent = Intent(context, QnaViewActivity::class.java)
                intent.putExtra("qnaInfo", clickedInfo)

                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QnaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_list_qna, parent, false)
        return QnaViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: QnaViewHolder, position: Int) {
        holder.medalName.text = items[position].medalName
        holder.qName.text = items[position].title
        holder.thumbNail.setImageResource(items[position].postImg ?: 0)
        holder.qInfo.text = items[position].postIn
        holder.time.text = items[position].writingTime
    }

    fun filterList(filter: String) { // 관심 자격증 분야만 필터링하여 출력하는 함수
        val myList = ArrayList<QnaData>()
        for (item in items) {
            if (item.medalName.equals(filter)) {
                myList.add(item)
            }
        }
        items.clear()
        items.addAll(myList)

        notifyDataSetChanged()
    }

    fun addAll(allItems: ArrayList<QnaData>) { // 전체 리스트로 다시 전환
        items.clear()
        items.addAll(allItems)

        notifyDataSetChanged()
    }

    fun addItem(item: QnaData) {
        items.add(item)
    }

    fun clearItem() {
        items.clear()
    }
}