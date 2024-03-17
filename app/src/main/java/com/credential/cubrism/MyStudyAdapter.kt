package com.credential.cubrism

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyStudyAdapter(private val items: ArrayList<String>) : RecyclerView.Adapter<MyStudyAdapter.StudyViewHolder>() {
    inner class StudyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val studyTitle = v.findViewById<TextView>(R.id.txtStudyGroupTitle)
        val enterBtn = v.findViewById<Button>(R.id.btnEnterStudy)

        init {
            enterBtn.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, StudyActivity::class.java)
                intent.putExtra("studyGroupTitle", studyTitle.text.toString())

                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list_mystudy, parent, false)

        return StudyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        holder.studyTitle.text = items[position]
    }

    fun addItem(item: String) {
        items.add(item)
    }

    fun clearItem() {
        items.clear()
    }
}