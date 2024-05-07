package com.credential.cubrism.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.ActivityMyscheduleBinding
import com.credential.cubrism.view.adapter.CalListAdapter
import com.credential.cubrism.viewmodel.CalendarViewModel

class MyScheduleListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyscheduleBinding.inflate(layoutInflater) }

    private val scheduleViewModel : ScheduleViewModel by viewModels { ViewModelFactory(ScheduleRepository()) }

    private val scheduleAdapter = ScheduleAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        scheduleViewModel.getScheduleList(null, null)
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = scheduleAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 16, 0, 0, 0, 0, null))
        }
    }

    private fun observeViewModel() {
        scheduleViewModel.apply {
            scheduleList.observe(this@MyScheduleListActivity) {
                scheduleAdapter.setItemList(it)
            }

            errorMessage.observe(this@MyScheduleListActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@MyScheduleListActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}