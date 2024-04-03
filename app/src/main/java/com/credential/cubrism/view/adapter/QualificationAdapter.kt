package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemDialogSearchBinding
import com.credential.cubrism.model.dto.QualificationListDto
import com.credential.cubrism.view.diff.QualificationDiffUtil

class QualificationAdapter : RecyclerView.Adapter<QualificationAdapter.ViewHolder>(), Filterable {
    private var itemList = mutableListOf<QualificationListDto>()
    private var filteredItemList = mutableListOf<QualificationListDto>()
    private var onItemClickListener: ((QualificationListDto, Int) -> Unit)? = null

    override fun getItemCount(): Int = filteredItemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDialogSearchBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredItemList[position])
    }

    inner class ViewHolder(private val binding: ItemDialogSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(filteredItemList[position], position)
                }
            }
        }

        fun bind(item: QualificationListDto) {
            binding.txtQualificationName.text = item.name
        }
    }

    fun setOnItemClickListener(listener: (QualificationListDto, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<QualificationListDto>) {
        val diffCallback = QualificationDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        itemList.clear()
        itemList.addAll(list)
        filteredItemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setFilteredList(list: List<QualificationListDto>) {
        val diffCallback = QualificationDiffUtil(filteredItemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        filteredItemList.clear()
        filteredItemList.addAll(list)
        diffResult.dispatchUpdatesTo(this@QualificationAdapter)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<QualificationListDto>()
                val query = constraint.toString().lowercase().replace(" ", "")

                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(itemList)
                } else {
                    itemList.forEach {
                        if (it.name.lowercase().replace(" ", "").contains(query)) {
                            filteredList.add(it)
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                val filteredList = results?.values as? List<QualificationListDto>
                filteredList?.let { setFilteredList(it) }
            }
        }
    }
}