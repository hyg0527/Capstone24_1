package com.credential.cubrism.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityMyscheduleBinding
import com.credential.cubrism.model.repository.ScheduleRepository
import com.credential.cubrism.view.adapter.ScheduleAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.ScheduleViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = scheduleAdapter

        scheduleAdapter.setOnItemClickListener { item, _ ->
            ScheduleInfoDialog(item,
                onUpdate = {
                    ScheduleAddUpdateDialog(
                        ScheduleType.UPDATE,
                        this,
                        null,
                        item,
                        onClick = {
                            scheduleViewModel.updateSchedule(item.scheduleId, it)
                        }
                    ).show(supportFragmentManager, "scheduleAddUpdate")
                },
                onDelete = {
                    AlertDialog.Builder(this).apply {
                        setMessage("일정을 삭제하시겠습니까?")
                        setNegativeButton("취소", null)
                        setPositiveButton("확인") { _, _ ->
                            scheduleViewModel.deleteSchedule(it)
                        }
                        show()
                    }
                }
            ).show(supportFragmentManager, "scheduleInfo")
        }
    }

    private fun observeViewModel() {
        scheduleViewModel.apply {
            scheduleList.observe(this@MyScheduleListActivity) { list ->
                binding.progressIndicator.visibility = View.GONE
                if (list.isEmpty())
                    binding.txtNoSchedule.visibility = View.VISIBLE
                else
                    binding.txtNoSchedule.visibility = View.GONE
                scheduleAdapter.setItemList(list)
            }

            updateSchedule.observe(this@MyScheduleListActivity) {
                scheduleViewModel.getScheduleList(null, null)
            }

            deleteSchedule.observe(this@MyScheduleListActivity) {
                scheduleViewModel.getScheduleList(null, null)
            }

            errorMessage.observe(this@MyScheduleListActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    if (!message.lowercase().contains("jwt"))
                        Toast.makeText(this@MyScheduleListActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}