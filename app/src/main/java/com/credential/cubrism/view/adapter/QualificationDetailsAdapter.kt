package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListQdFileBinding
import com.credential.cubrism.databinding.ItemListQdHeaderBinding
import com.credential.cubrism.databinding.ItemListQdTextBinding
import com.credential.cubrism.model.dto.Fee
import com.credential.cubrism.model.dto.File
import com.credential.cubrism.view.diff.QualificationDetailsDiffUtil

enum class ItemType {
    HEADER, SCHEDULE, FEE, TENDENCY, STANDARD, QUESTION, ACQUISITION, BOOK
}

data class QualificationDetailsItem(
    val type: ItemType,
    val data: Any
)

class QualificationDetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = mutableListOf<QualificationDetailsItem>()
    private var onItemClickListener: ((Any, Int) -> Unit)? = null

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].type) {
            ItemType.HEADER -> 0
            ItemType.SCHEDULE -> 1
            ItemType.FEE -> 2
            ItemType.TENDENCY -> 3
            ItemType.STANDARD -> 4
            ItemType.QUESTION -> 5
            ItemType.ACQUISITION -> 6
            ItemType.BOOK -> 7
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> HeaderViewHolder(ItemListQdHeaderBinding.inflate(inflater, parent, false))
            1 -> ScheduleViewHolder(ItemListQdTextBinding.inflate(inflater, parent, false))
            2 -> FeeViewHolder(ItemListQdTextBinding.inflate(inflater, parent, false))
            3 -> TendencyViewHolder(ItemListQdTextBinding.inflate(inflater, parent, false))
            4 -> StandardViewHolder(ItemListQdFileBinding.inflate(inflater, parent, false))
            5 -> QuestionViewHolder(ItemListQdFileBinding.inflate(inflater, parent, false))
            6 -> AcquisitionViewHolder(ItemListQdTextBinding.inflate(inflater, parent, false))
            else -> BookViewHolder(ItemListQdTextBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(itemList[position].data as String)
            is ScheduleViewHolder -> holder.bind(itemList[position].data as String)
            is FeeViewHolder -> holder.bind(itemList[position].data as Fee)
            is TendencyViewHolder -> holder.bind(itemList[position].data as String)
            is StandardViewHolder -> holder.bind(itemList[position].data as File)
            is QuestionViewHolder -> holder.bind(itemList[position].data as File)
            is AcquisitionViewHolder -> holder.bind(itemList[position].data as String)
            is BookViewHolder -> holder.bind(itemList[position].data as String)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemListQdHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.txtHeader.text = item
        }
    }

    inner class ScheduleViewHolder(private val binding: ItemListQdTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.txtText.text = item
        }
    }

    inner class FeeViewHolder(private val binding: ItemListQdTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Fee) {
            binding.txtText.text = "필기 : ${item.writtenFee}원\n실기 : ${item.practicalFee}원"
        }
    }

    inner class TendencyViewHolder(private val binding: ItemListQdTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.txtText.text = item.replace(" ", "\u00A0")
        }
    }

    inner class StandardViewHolder(private val binding: ItemListQdFileBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position].data, position)
                }
            }
        }

        fun bind(item: File) {
            Glide.with(binding.root).load(getFileIcon(item)).into(binding.imgFile)
            binding.txtFile.text = item.fileName.replace(" ", "\u00A0")
        }
    }

    inner class QuestionViewHolder(private val binding: ItemListQdFileBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position].data, position)
                }
            }
        }

        fun bind(item: File) {
            Glide.with(binding.root).load(getFileIcon(item)).into(binding.imgFile)
            binding.txtFile.text = item.fileName.replace(" ", "\u00A0")
        }
    }

    inner class AcquisitionViewHolder(private val binding: ItemListQdTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.txtText.text = item.replace(" ", "\u00A0")
        }
    }

    inner class BookViewHolder(private val binding: ItemListQdTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.txtText.text = item
        }
    }

    fun setOnItemClickListener(listener: (Any, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setItemList(list: List<QualificationDetailsItem>) {
        val diffCallBack = QualificationDetailsDiffUtil(itemList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        itemList.clear()
        itemList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getFileIcon(item: File): Int {
        return when (item.fileName.substringAfterLast('.')) {
            "pdf" -> R.drawable.icon_pdf
            "hwp" -> R.drawable.icon_hwp
            "zip" -> R.drawable.icon_zip
            else -> R.drawable.icon_etc
        }
    }
}