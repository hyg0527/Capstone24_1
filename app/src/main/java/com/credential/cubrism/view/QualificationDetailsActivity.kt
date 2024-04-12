package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityQualificationDetailsBinding
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.view.adapter.StandardAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QualificationDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQualificationDetailsBinding.inflate(layoutInflater) }

    private val qualificationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }

    private val standardAdapter = StandardAdapter()

    private val qualificationName by lazy { intent.getStringExtra("qualificationName") }
    private val qualificationCode by lazy { intent.getStringExtra("qualificationCode") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        binding.txtTitle.text = qualificationName
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupView() {
        qualificationCode?.let {
            qualificationViewModel.getQualificationDetails(it)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerStandard.apply {
            adapter = standardAdapter
            addItemDecoration(ItemDecoratorDivider(0, 32, 0, 0, 0, 0, null))
            setHasFixedSize(true)
        }

        standardAdapter.setOnItemClickListener { item, _ ->
            Toast.makeText(this, item.filePath, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        qualificationViewModel.apply {
            qualificationDetails.observe(this@QualificationDetailsActivity) { result ->
                binding.txtFee.text = "필기 : ${result.fee.writtenFee}원\n실기 : ${result.fee.practicalFee}원" // 수수료
                binding.txtTendency.text = result.tendency // 출제경향
                binding.txtAcquistion.text = result.acquisition // 취득방법

                standardAdapter.setItemList(result.standard)
            }

            errorMessage.observe(this@QualificationDetailsActivity) { message ->
                message.getContentIfNotHandled()?.let { Toast.makeText(this@QualificationDetailsActivity, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }
}