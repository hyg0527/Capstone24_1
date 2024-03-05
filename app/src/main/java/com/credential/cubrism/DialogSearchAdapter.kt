package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

// ItemClickListener 인터페이스 정의
interface SearchItemClickListener {
    fun onItemClick(item: DialogItem)
}

data class DialogItem(val name: String? = null, val image: Int? = null)

class DialogSearchAdapter(private val items: ArrayList<DialogItem>) : RecyclerView.Adapter<DialogSearchAdapter.DialogViewHolder>()
    , Filterable {
    private var filteredItems: List<DialogItem> = items
    private var itemClickListener: SearchItemClickListener? = null
    fun setItemClickListener(listener: SearchItemClickListener) {
        itemClickListener = listener
    }

    inner class DialogViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = v.findViewById<TextView>(R.id.nameSearch)
        init {
            v.setOnClickListener {
                val position = adapterPosition
                val clickedItem = filteredItems[position]
                itemClickListener?.onItemClick(clickedItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_dialog_search, parent, false)
        return DialogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredItems.count()
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
        holder.name.text = filteredItems[position].name
    }

    override fun getFilter(): Filter { // 검색어 필터 기능 활성화
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString().trim()
                val filteredItems = ArrayList<DialogItem>()

                if (charString.isEmpty()) {
                    filteredItems.addAll(items)
                } else {
                    for (item in items) {
                        item.name?.let { // null인지 탐지, null이면 무시하고 아니면 반환
                            if (it.lowercase().contains(charString)) {
                                filteredItems.add(item)
                            }
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                filteredItems = results?.values as List<DialogItem>
                notifyDataSetChanged()
            }
        }
    }
}