package com.credential.cubrism.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.ActivityMyscheduleBinding
import com.credential.cubrism.view.adapter.CalListAdapter
import com.credential.cubrism.viewmodel.CalendarViewModel

class MyScheduleListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyscheduleBinding.inflate(layoutInflater)}
    private val calendarViewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.myScheduleView.adapter = CalListAdapter(calendarViewModel.calMonthList.value ?: ArrayList())
        binding.myScheduleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}