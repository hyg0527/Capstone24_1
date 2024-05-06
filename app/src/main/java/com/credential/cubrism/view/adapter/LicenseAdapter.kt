package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemLicenseDefaultBinding
import com.credential.cubrism.databinding.ItemListMylicenselistBinding


data class myLicenseData(val myLCStxt: String? = null)

class LicenseAdapter(private val items: ArrayList<myLicenseData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class LCSViewHolder(val binding: ItemListMylicenselistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: myLicenseData) {
            val position = adapterPosition
            binding.apply {
                txtQualificationName.text = item.myLCStxt
                view.background = ResourcesCompat.getDrawable(itemView.resources, R.color.blue, null)
                yellowpin.setImageResource(R.drawable.yellow_pin)

                // 배경색을 번갈아서 출력
                if ((position + 1) % 2 == 0) {
                    view.background = ResourcesCompat.getDrawable(itemView.resources, R.color.mdblue, null)
                } else {
                    view.background = ResourcesCompat.getDrawable(itemView.resources, R.color.blue, null)
                }

                // pin의 색을 번갈아서 출력
                if ((position + 1) % 3 == 0) {
                    yellowpin.setImageResource(R.drawable.red_pin)
                } else if ((position + 1) % 3 == 1) {
                    yellowpin.setImageResource(R.drawable.yellow_pin)
                } else {
                    yellowpin.setImageResource(R.drawable.green_pin)
                }
            }
        }
    }

    inner class EmptyViewHolder(val binding: ItemLicenseDefaultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {}
    }

    companion object {
        private const val ITEM_TYPE_NORMAL = 0
        private const val ITEM_TYPE_EMPTY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ITEM_TYPE_NORMAL -> {
                val binding = ItemListMylicenselistBinding.inflate(layoutInflater, parent, false)
                LCSViewHolder(binding)
            }
            ITEM_TYPE_EMPTY -> {
                val binding = ItemLicenseDefaultBinding.inflate(layoutInflater, parent, false)
                EmptyViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LCSViewHolder -> {
                holder.bind(items[position])
            }
            is EmptyViewHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) ITEM_TYPE_EMPTY else ITEM_TYPE_NORMAL
    }
}