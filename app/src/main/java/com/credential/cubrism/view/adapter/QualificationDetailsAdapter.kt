package com.credential.cubrism.view.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ItemListQdBookBinding
import com.credential.cubrism.databinding.ItemListQdFileBinding
import com.credential.cubrism.databinding.ItemListQdHeaderBinding
import com.credential.cubrism.databinding.ItemListQdScheduleBinding
import com.credential.cubrism.databinding.ItemListQdTextBinding
import com.credential.cubrism.model.dto.Book
import com.credential.cubrism.model.dto.Fee
import com.credential.cubrism.model.dto.File
import com.credential.cubrism.model.dto.Schedule
import com.credential.cubrism.view.diff.QualificationDetailsDiffUtil
import java.text.DecimalFormat

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

    private val dec = DecimalFormat("#,###")

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
            1 -> ScheduleViewHolder(ItemListQdScheduleBinding.inflate(inflater, parent, false))
            2 -> FeeViewHolder(ItemListQdTextBinding.inflate(inflater, parent, false))
            3 -> TendencyViewHolder(ItemListQdTextBinding.inflate(inflater, parent, false))
            4 -> StandardViewHolder(ItemListQdFileBinding.inflate(inflater, parent, false))
            5 -> QuestionViewHolder(ItemListQdFileBinding.inflate(inflater, parent, false))
            6 -> AcquisitionViewHolder(ItemListQdTextBinding.inflate(inflater, parent, false))
            else -> BookViewHolder(ItemListQdBookBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(itemList[position].data as String)
            is ScheduleViewHolder -> holder.bind(itemList[position].data as Schedule)
            is FeeViewHolder -> holder.bind(itemList[position].data as Fee)
            is TendencyViewHolder -> holder.bind(itemList[position].data as String)
            is StandardViewHolder -> holder.bind(itemList[position].data as File)
            is QuestionViewHolder -> holder.bind(itemList[position].data as File)
            is AcquisitionViewHolder -> holder.bind(itemList[position].data as String)
            is BookViewHolder -> holder.bind(itemList[position].data as Book)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemListQdHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.txtHeader.text = item
        }
    }

    inner class ScheduleViewHolder(private val binding: ItemListQdScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Schedule) {
            binding.txtCategory.text = item.category
            setGravityAndText(binding.txtWrittenApp, item.writtenApp)
            setGravityAndText(binding.txtWrittenExam, item.writtenExam)
            setGravityAndText(binding.txtWrittenExamResult, item.writtenExamResult)
            setGravityAndText(binding.txtPracticalApp, item.practicalApp)
            setGravityAndText(binding.txtPracticalExam, item.practicalExam)
            setGravityAndText(binding.txtPracticalExamResult, item.practicalExamResult)
        }
    }

    inner class FeeViewHolder(private val binding: ItemListQdTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Fee) {
            val writtenFee = if (item.writtenFee != null) "필기 : ${dec.format(item.writtenFee)}원" else ""
            val practicalFee = if (item.practicalFee != null) "실기 : ${dec.format(item.practicalFee)}원" else ""
            binding.txtText.text = if (writtenFee.isNotEmpty() && practicalFee.isNotEmpty()) "$writtenFee\n$practicalFee" else writtenFee.plus(practicalFee)
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

    inner class BookViewHolder(private val binding: ItemListQdBookBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(itemList[position].data, position)
                }
            }
        }

        fun bind(item: Book) {
            Glide.with(binding.root)
                .load(item.thumbnail)
                .placeholder(ColorDrawable(Color.TRANSPARENT))
                .transition(DrawableTransitionOptions.withCrossFade())
                .sizeMultiplier(0.7f)
                .error(R.drawable.icon_book)
                .fallback(R.drawable.icon_book)
                .into(binding.imgThumbnail)
            binding.txtTitle.text = item.title
            binding.txtAuthors.text = "저자 : ${item.authors}"
            binding.txtPublisher.text = "출판 : ${item.publisher}"
            binding.txtPrice.text = "${dec.format(item.price)}원"
            binding.txtDate.text = item.date
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

    private fun setGravityAndText(textView: TextView, text: String?) {
        textView.text = text?.replace("~", " ~ ")
        if (text != null && Regex("\\d{4}\\.\\d{2}\\.\\d{2}").containsMatchIn(text)) {
            textView.gravity = Gravity.END
        } else {
            textView.gravity = Gravity.START
        }
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