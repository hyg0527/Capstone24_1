package com.credential.cubrism.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.ActivityMyscheduleBinding
import com.credential.cubrism.view.adapter.CalListAdapter
import com.credential.cubrism.viewmodel.CalMonthViewModel

class MyScheduleListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyscheduleBinding.inflate(layoutInflater)}
    private lateinit var calendarViewModel: CalMonthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        calendarViewModel = ViewModelProvider(this)[CalMonthViewModel::class.java]

        binding.backBtn.setOnClickListener { finish() }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        calendarViewModel.calMonthList.observe(this, Observer {calMonthList ->
            binding.myScheduleView.adapter = CalListAdapter(calMonthList ?: ArrayList())
            binding.myScheduleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        })
    }
}