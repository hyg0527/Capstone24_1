package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityQualificationMiddleBinding
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.view.adapter.MiddleFieldAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QualificationMiddleActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQualificationMiddleBinding.inflate(layoutInflater) }

    private val qualificationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }

    private val middleFieldAdapter = MiddleFieldAdapter()

    private val majorFieldName by lazy { intent.getStringExtra("majorFieldName") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        binding.txtTitle.text = majorFieldName
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = middleFieldAdapter
            addItemDecoration(ItemDecoratorDivider(this@QualificationMiddleActivity, 0, 16, 0, 0, 0, 0, null))
            setHasFixedSize(true)
        }

        middleFieldAdapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, QualificationDetailsActivity::class.java)
            intent.putExtra("qualificationName", item.name)
            intent.putExtra("qualificationCode", item.code)
            startActivity(intent)
        }
    }

    private fun setupView() {
        majorFieldName?.let {
            binding.progressIndicator.show()
            qualificationViewModel.getMiddleFieldList(it)
        }
    }

    private fun observeViewModel() {
        qualificationViewModel.apply {
            middleFieldList.observe(this@QualificationMiddleActivity) { result ->
                binding.progressIndicator.hide()
                middleFieldAdapter.setItemList(result)
            }

            errorMessage.observe(this@QualificationMiddleActivity) { message ->
                message.getContentIfNotHandled()?.let { Toast.makeText(this@QualificationMiddleActivity, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }
}