package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListMypageCertBinding
import com.credential.cubrism.view.diff.StringListDiffUtil

data class myCertData(val name: String? = null, var num: Int? = null)

class MyPageCertAdapter(private val items: ArrayList<myCertData>) : RecyclerView.Adapter<MyPageCertAdapter.ViewHolder>() {
    private var itemList = mutableListOf<String>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListMypageCertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(val binding: ItemListMypageCertBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                val item = items[position]
                items.remove(item)

                rearrangeItem(items)
                notifyDataSetChanged()
            }
        }
        fun bind(item: myCertData) {
            binding.txtCategory.text = item.name
            binding.certCount.text = "${item.num}"
        }
    }

    fun rearrangeItem(items: ArrayList<myCertData>) {
        var i = 1
        for (item in items) {
            item.num = i
            i++
        }
    }

    fun setItemList(list: List<String>) {
        val diffCallBack = StringListDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }
}