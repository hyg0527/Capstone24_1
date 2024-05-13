package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.MyApplication
import com.credential.cubrism.databinding.ActivityNotiBinding
import com.credential.cubrism.model.repository.NotiRepository
import com.credential.cubrism.view.adapter.NotiAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider

class NotiActivity : AppCompatActivity() {
    private val binding by lazy { ActivityNotiBinding.inflate(layoutInflater) }

    private val notiRepository = NotiRepository(MyApplication.getInstance().getNotiDao())

    private val notiAdapter = NotiAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = notiAdapter
            addItemDecoration(ItemDecoratorDivider(0, 60, 0, 0, 0, 0, null))
        }

        notiAdapter.setOnItemClickListener { item, _ ->
            when (item.type) {
                "POST" -> {
                    val intent = Intent(this, PostViewActivity::class.java)
                    intent.putExtra("postId", item.id.toInt())
                    startActivity(intent)
                }
                "STUDY" -> {
                    if (item.id.isNotEmpty()) {
                        val intent = Intent(this, StudyActivity::class.java)
                        intent.putExtra("studyGroupId", item.id.toInt())
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        notiRepository.getAllNoties().observe(this) {
            if (it.isEmpty()) {
                binding.txtEmpty.visibility = android.view.View.VISIBLE
            } else {
                binding.txtEmpty.visibility = android.view.View.GONE
            }
            notiAdapter.setItemList(it)
        }
    }
}