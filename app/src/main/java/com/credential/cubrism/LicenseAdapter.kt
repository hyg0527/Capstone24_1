package com.credential.cubrism

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


data class myLicenseData(val myLCStxt: String? = null) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(myLCStxt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<myLicenseData> {
        override fun createFromParcel(parcel: Parcel): myLicenseData {
            return myLicenseData(parcel)
        }

        override fun newArray(size: Int): Array<myLicenseData?> {
            return arrayOfNulls(size)
        }
    }

}


class LicenseAdapter(private val items: ArrayList<myLicenseData>) : RecyclerView.Adapter<LicenseAdapter.LCSViewHolder>() {

    inner class LCSViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val myLCStxt = v.findViewById<TextView>(R.id.myLCStxt)
        val myLCSimage = v.findViewById<ImageView>(R.id.myLCSimage)
        val myLCSpin = v.findViewById<ImageView>(R.id.yellowpin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LCSViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_list_mylicenselist, parent, false)
        return LCSViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: LCSViewHolder, position: Int) {

        holder.myLCStxt.text = items[position].myLCStxt
        holder.myLCSimage.setImageResource(R.drawable.button_rounded_corner_blue3)
        holder.myLCSpin.setImageResource(R.drawable.yellow_pin)

        // 배경색을 번갈아서 출력
        if ((position + 1) % 2 == 0) {
            holder.myLCSimage.setImageResource(R.drawable.button_rounded_corner_mdblue)
        } else {
            holder.myLCSimage.setImageResource(R.drawable.button_rounded_corner_blue3)
        }

        // pin의 색을 번갈아서 출력
        if ((position + 1) % 3 == 0) {
            holder.myLCSpin.setImageResource(R.drawable.red_pin)
        } else if ((position + 1) % 3 == 1) {
            holder.myLCSpin.setImageResource(R.drawable.yellow_pin)
        } else {
            holder.myLCSpin.setImageResource(R.drawable.green_pin)
        }



    }


    fun addItem(item: myLicenseData) {
        items.add(item)
    }

    fun clearItem() {
        items.clear()
    }
}