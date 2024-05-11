package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.ActivityStudygroupGoalBinding
import com.credential.cubrism.databinding.DialogGoalAddBinding
import com.credential.cubrism.view.adapter.GoalAdapter
import com.credential.cubrism.view.adapter.Goals

class StudyManageGoalActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudygroupGoalBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()

        val adapter = initGoalRecyclerView()

        binding.btnAddGoal.setOnClickListener {
            if (adapter.getItem().size >= 3) {
                Toast.makeText(this, "목표 개수는 3개까지 작성 가능합니다.", Toast.LENGTH_SHORT).show()
            } else {
                callAddDialog(adapter)
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initGoalRecyclerView(): GoalAdapter {
        val items = ArrayList<Goals>()
        val adapter = GoalAdapter(items, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        return adapter
    }

    private fun callAddDialog(adapter: GoalAdapter) { // 목표 추가 다이얼로그 호출
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogGoalAddBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
            .setPositiveButton("추가") { dialog, _ ->
                if (dialogBinding.editTextGoalTitle.text.isEmpty()) {
                    Toast.makeText(this, "목표 제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                else {
                    adapter.addItem(adapter.getItem().size + 1, dialogBinding.editTextGoalTitle.text.toString())
                    Toast.makeText(this, "목표가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}