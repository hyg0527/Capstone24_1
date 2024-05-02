package com.credential.cubrism.view.adapter

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListScheduleBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

interface ScheduleClickListener {
    fun onItemClick(item: CalMonth)
}

data class CalMonth(val startDate: String? = null, val endDate: String? = null,
                    val title: String? = null, val content: String? = null, val allDay: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(content)
        parcel.writeByte(if (allDay) 1 else 0)
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
class CalListAdapter(private var items: ArrayList<CalMonth>) : RecyclerView.Adapter<CalListAdapter.CalViewHolder>() {
    private var itemClickListener: ScheduleClickListener? = null
    fun setItemClickListener(listener: ScheduleClickListener) {
        itemClickListener = listener
    }

    inner class CalViewHolder(private val binding: ItemListScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalMonth) {
            binding.apply {
                txtCalMonthTitle.text = item.title
                timeStart.text = convertStartEndTxt(item.startDate)
                timeEnd.text = convertStartEndTxt(item.endDate)
                txtCalMonthInfo.text = item.content
                root.setOnClickListener {
                    itemClickListener?.onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListScheduleBinding.inflate(inflater, parent, false)
        return CalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CalViewHolder, position: Int) {
        holder.bind(items[position])
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
            val item2 = checkFormat(item)
            val (realDateStartDay, realDateEndDay, dateToInt) = findDate(item2, date)

            if ((realDateStartDay <= dateToInt) && (realDateEndDay >= dateToInt)) {
                newList.add(item2)
            }
        }

        items.clear()
        items.addAll(newList)

        notifyDataSetChanged()

        return items.isNotEmpty()
    }

    private fun checkFormat(value: CalMonth): CalMonth { // 형식 체크(로컬데이터 형식으로 변환 후 반환)
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val dateFormatterOutput = SimpleDateFormat("yyyy - MM - dd a hh:mm", Locale.KOREA)
        val dateFormatterOutputAllDay = SimpleDateFormat("yyyy - MM - dd 종일", Locale.getDefault())

        try { // startDate를 dateFormatter로 파싱하여 오류가 없으면 value 그대로 반환
            dateFormatterOutput.parse(value.startDate ?: "")
            return value
        }
        catch (e: ParseException) { // ParseException이 발생 하면 format 변경
            var startDate = ""; var endDate = ""

            if (value.allDay) {
                startDate = dateFormatterOutputAllDay.format(dateFormatter.parse(value.startDate ?: "") ?: "")
                endDate = dateFormatterOutputAllDay.format(dateFormatter.parse(value.startDate ?: "") ?: "")
            }
            else {
                startDate = dateFormatterOutput.format(dateFormatter.parse(value.startDate ?: "") ?: "")
                endDate = dateFormatterOutput.format(dateFormatter.parse(value.endDate ?: "") ?: "")
            }

            return value.copy(startDate = startDate, endDate = endDate)
        }
    }

    fun revertFormat(value: CalMonth): CalMonth { // 형식 체크(원래 데이터 형식으로 다시 변환 후 반환)
        val dateFormatter = SimpleDateFormat("yyyy - MM - dd a hh:mm", Locale.KOREA)
        val dateFormatterAllDay = SimpleDateFormat("yyyy - MM - dd 종일", Locale.getDefault())
        val dateFormatterOutput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val dateFormatterOutputAllDay = SimpleDateFormat("yyyy-MM-dd'T'00:00", Locale.getDefault())

        var startDate = ""; var endDate = ""

        if (value.allDay) {
            startDate = dateFormatterOutputAllDay.format(dateFormatterAllDay.parse(value.startDate ?: "") ?: "")
            endDate = dateFormatterOutputAllDay.format(dateFormatterAllDay.parse(value.endDate ?: "") ?: "")
        }
        else {
            startDate = dateFormatterOutput.format(dateFormatter.parse(value.startDate ?: "") ?: "")
            endDate = dateFormatterOutput.format(dateFormatter.parse(value.endDate ?: "") ?: "")
        }

        return value.copy(startDate = startDate, endDate = endDate)
    }

    private fun findDate(item: CalMonth, date: String): Triple<Int, Int, Int> {
        var realDateStart = ""
        var realDateEnd = ""
        val dateToInt = date.replace(" - ", "").toInt()

        if ((item.startDate ?: "").contains("종일") || (item.endDate ?: "").contains("종일")) {
            realDateStart = item.startDate?.substringBefore(" 종일")?.trim() ?: ""
            realDateEnd = item.endDate?.substringBefore(" 종일")?.trim() ?: ""
        }
        else if ((item.startDate ?: "").contains("오")  || (item.endDate ?: "").contains("오")) {
            realDateStart = item.startDate?.substringBefore(" 오")?.trim() ?: ""
            realDateEnd = item.endDate?.substringBefore(" 오")?.trim() ?: ""
        }
        else return Triple(0,0,0)

        val realDateStartDay = realDateStart.replace(" - ", "").toInt()
        val realDateEndDay = realDateEnd.replace(" - ", "").toInt()

        return Triple(realDateStartDay, realDateEndDay, dateToInt)
    }

    fun addItem(item: CalMonth) { // 일정 항목 추가 함수
        items.add(item)
    }

    fun clearItem() {
        items.clear()
    }
}