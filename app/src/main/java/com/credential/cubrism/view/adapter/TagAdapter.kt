package com.credential.cubrism

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Tags(val tag: String? = null, var isEnabled: Boolean = false): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tag)
        parcel.writeByte(if (isEnabled) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tags> {
        override fun createFromParcel(parcel: Parcel): Tags {
            return Tags(parcel)
        }

        override fun newArray(size: Int): Array<Tags?> {
            return arrayOfNulls(size)
        }
    }
}

class TagAdapter(private val items: ArrayList<Tags>, val isList: Boolean, val isBlue: Boolean = true)
    : RecyclerView.Adapter<TagAdapter.TapViewHolder>() {
    inner class TapViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tag = v.findViewById<TextView>(R.id.txtTag)

        init {
            val position = adapterPosition
            val item = items[position]

            v.setOnClickListener {
                if (isList) {
                    if (!item.isEnabled) {
                        item.isEnabled = true
                        tag.setBackgroundResource(R.drawable.tag_rounded_corner_selected)
                    } else {
                        item.isEnabled = false
                        tag.setBackgroundResource(R.drawable.tag_rounded_corner)
                    }
                }
            }

            if (!isBlue) {
                item.isEnabled = false
                tag.setBackgroundResource(R.drawable.tag_rounded_corner_study)
                tag.resources.getColor(R.color.white, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TapViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list_study_tag, parent, false)

        return TapViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: TapViewHolder, position: Int) {
        holder.tag.text = items[position].tag

        if (items[position].isEnabled) {
            holder.tag.setBackgroundResource(R.drawable.tag_rounded_corner_selected)
        }
    }

    fun changeTagStatus(newItem: ArrayList<Tags>) {
        for (item in items) {
            val matchingItem = newItem.find { it.tag.equals(item.tag, ignoreCase = true) }
            if (matchingItem != null) {
                item.isEnabled = true
            }
        }

        notifyDataSetChanged()
    }
}