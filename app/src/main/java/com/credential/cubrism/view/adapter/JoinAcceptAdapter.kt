package com.credential.cubrism.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.databinding.ItemListJoinacceptBinding

class JoinAcceptAdapter(private val items: ArrayList<String>) : RecyclerView.Adapter<JoinAcceptAdapter.AcceptViewHolder>() {
    inner class AcceptViewHolder(val binding: ItemListJoinacceptBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnAccept.setOnClickListener { handleButtonClick(true) }
            binding.btnDecline.setOnClickListener { handleButtonClick(false) }
        }

        fun bind(item: String) {
            binding.txtUserName.text = item
        }

        private fun handleButtonClick(accept: Boolean) {
            val position = adapterPosition

            if (position != RecyclerView.NO_POSITION) {
                val item = items[position]
                if (accept) {
                    Toast.makeText(itemView.context, "요청을 수락하였습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(itemView.context, "요청을 거절하였습니다.", Toast.LENGTH_SHORT).show()
                }

                items.remove(item)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptViewHolder {
        val binding = ItemListJoinacceptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AcceptViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AcceptViewHolder, position: Int) {
        holder.bind(items[position])
    }
}