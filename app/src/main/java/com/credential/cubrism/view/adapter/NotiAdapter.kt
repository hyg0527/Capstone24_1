package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListNotiBinding
import com.credential.cubrism.view.diff.NotiDiffUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Noti(
    val notiId: Long,
    val title: String,
    val body: String,
    val type: String,
    val id: String,
    var isHeader: Boolean?,
    val date: LocalDateTime
)

class NotiAdapter : RecyclerView.Adapter<NotiAdapter.ViewHolder>() {
    private var itemList = mutableListOf<Noti>()
    private var onItemClickListener: ((Noti, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListNotiBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class ViewHolder(private val binding: ItemListNotiBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position], position)
                }
            }
        }

        fun bind(item: Noti) {
            when (item.type) {
                "POST" -> Glide.with(itemView).load(R.drawable.qnaicon).into(binding.imgIcon)
                "STUDY" -> Glide.with(itemView).load(R.drawable.studybannericon).into(binding.imgIcon)
                else -> Glide.with(itemView).load(R.drawable.logo).into(binding.imgIcon)
            }
            binding.divider.visibility = if (item.isHeader == true && adapterPosition != 0) View.VISIBLE else View.GONE
            binding.txtDate.apply {
                if (item.isHeader == true) {
                    text = item.date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }
            binding.txtTitle.text = item.title
            binding.txtBody.text = item.body.replace(" ", "\u00A0")
            binding.txtTime.text = item.date.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    fun setOnItemClickListener(listener: (Noti, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<Noti>) {
        val sortedList = list.sortedByDescending { it.date }
        val groupedItems = sortedList.groupBy { it.date.toLocalDate() }
        val newList = mutableListOf<Noti>()
        for ((_, group) in groupedItems) {
            val items = group.mapIndexed { index, notiEntity ->
                notiEntity.isHeader = index == 0
                notiEntity
            }
            newList.addAll(items)
        }
        val diffCallBack = NotiDiffUtil(itemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}