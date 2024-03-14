package com.credential.cubrism

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


data class BannerData(val bannertxt: String? = null)
class BannerAdapter(private val items: ArrayList<BannerData>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val bannerimg = v.findViewById<ImageView>(R.id.imageView8)
        val bannertxt = v.findViewById<TextView>(R.id.textView40)
        val bannerbtn = v.findViewById<Button>(R.id.quitBtn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_list_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bannerimg.setImageResource(R.drawable.qnaicon)
        holder.bannertxt.text = items[position].bannertxt

    }

}