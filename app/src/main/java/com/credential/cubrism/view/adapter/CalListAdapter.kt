package com.credential.cubrism.view.adapter

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import java.text.SimpleDateFormat
import java.util.Locale

interface ScheduleClickListener {
    fun onItemClick(item: CalMonth)
}

data class CalMonth(val title: String? = null, val startTime: String? = null,
                    val endTime: String? = null, val info: String? = null, val isFullTime: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(info)
        parcel.writeByte(if (isFullTime) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CalMonth> {
        override fun createFromParcel(parcel: Parcel): CalMonth {
            return CalMonth(parcel)
        }

        override fun newArray(size: Int): Array<CalMonth?> {
            return arrayOfNulls(size)
        }
    }

}
// 일정 리스트 구현 adapter
class CalMonthListAdapter(private var items: ArrayList<CalMonth>) : RecyclerView.Adapter<CalMonthListAdapter.CalViewHolder>() {
    private var itemClickListener: ScheduleClickListener? = null
    fun setItemClickListener(listener: ScheduleClickListener) {
        itemClickListener = listener
    }

    inner class CalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title = v.findViewById<TextView>(R.id.txtCalMonthTitle)
        val timeStart = v.findViewById<TextView>(R.id.timeStart)
        val timeEnd = v.findViewById<TextView>(R.id.timeEnd)
        val info = v.findViewById<TextView>(R.id.txtCalMonthInfo)
        init {
            v.setOnClickListener {
                val position = adapterPosition
                val item = items[position]
                itemClickListener?.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list_calmonth, parent, false)

        return CalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: CalViewHolder, position: Int) {
        holder.title.text = items[position].title
        holder.timeStart.text = convertStartEndTxt(items[position].startTime)
        holder.timeEnd.text = convertStartEndTxt(items[position].endTime)
        holder.info.text = items[position].info
    }

    private fun convertStartEndTxt(inputString: String?): String {
        val inputDateString = inputString ?: ""
        val inputFormat = SimpleDateFormat("yyyy - MM - dd a hh:mm", Locale.KOREA)
        val outputFormat = SimpleDateFormat("MM.dd  h:mm a", Locale.ENGLISH)

        return if (inputDateString.contains("종일"))
            inputDateString.take(inputDateString.length - 3)
        else {
            val date = inputFormat.parse(inputDateString)
            return if (date != null) {
                outputFormat.format(date)
            } else {
                "Invalid date"
            }
        }
    }

    fun updateList(date: String): Boolean { // 날짜에 맞는 일정만 화면에 출력하는 함수
        val newList = ArrayList<CalMonth>()
        newList.clear()

        for (item in items) {
            val (realDateStartDay, realDateEndDay, dateToInt) = findDate(item, date)

            if ((realDateStartDay <= dateToInt) && (realDateEndDay >= dateToInt)) {
                newList.add(item)
            }
        }

        items.clear()
        items.addAll(newList)

        notifyDataSetChanged()

        return items.isNotEmpty()
    }

    private fun findDate(item: CalMonth, date: String): Triple<Int, Int, Int> {
        var realDateStart = ""
        var realDateEnd = ""
        val dateToInt = date.replace(" - ", "").toInt()

        if ((item.startTime ?: "").contains("종일") || (item.endTime ?: "").contains("종일")) {
            realDateStart = item.startTime?.substringBefore(" 종일")?.trim() ?: ""
            realDateEnd = item.endTime?.substringBefore(" 종일")?.trim() ?: ""
        }
        else if ((item.startTime ?: "").contains("오")  || (item.endTime ?: "").contains("오")) {
            realDateStart = item.startTime?.substringBefore(" 오")?.trim() ?: ""
            realDateEnd = item.endTime?.substringBefore(" 오")?.trim() ?: ""
        }
        else return Triple(0,0,0)

        val realDateStartDay = realDateStart.replace(" - ", "").toInt()
        val realDateEndDay = realDateEnd.replace(" - ", "").toInt()

        return Triple(realDateStartDay, realDateEndDay, dateToInt)
    }

    fun addItem(item: CalMonth) { // 일정 항목 추가 함수
        items.add(item)
    }

    fun removeItem(item: CalMonth) {
        items.remove(item)
    }

    fun clearItem() {
        items.clear()
    }
}