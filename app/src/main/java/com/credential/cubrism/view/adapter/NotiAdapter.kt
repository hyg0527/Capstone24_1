package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListNotiBinding
import com.credential.cubrism.model.entity.NotiEntity
import com.credential.cubrism.view.diff.NotiDiffUtil
import java.time.format.DateTimeFormatter

class NotiAdapter : RecyclerView.Adapter<NotiAdapter.ViewHolder>() {
    private var itemList = mutableListOf<NotiEntity>()
    private var onItemClickListener: ((NotiEntity, Int) -> Unit)? = null

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

        fun bind(item: NotiEntity) {
            binding.txtTitle.text = item.title
            binding.txtBody.text = item.body.replace(" ", "\u00A0")
            binding.txtDate.text = item.date.format(DateTimeFormatter.ofPattern("M/dd HH:mm"))
        }
    }

    fun setOnItemClickListener(listener: (NotiEntity, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<NotiEntity>) {
        val diffCallBack = NotiDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}