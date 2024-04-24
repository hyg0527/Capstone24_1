package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.credential.cubrism.databinding.ActivityQualificationDetailsBinding
import com.credential.cubrism.model.dto.Book
import com.credential.cubrism.model.dto.File
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.view.adapter.ItemType
import com.credential.cubrism.view.adapter.QualificationDetailsAdapter
import com.credential.cubrism.view.adapter.QualificationDetailsItem
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QualificationDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQualificationDetailsBinding.inflate(layoutInflater) }

    private val qualificationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }

    private val qualificationDetailsAdapter = QualificationDetailsAdapter()

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
            binding.progressIndicator.show()
            qualificationViewModel.getQualificationDetails(it)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = qualificationDetailsAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 40, 0, 0, 0, 0, null))
        }

        qualificationDetailsAdapter.setOnItemClickListener { item, _ ->
            when (item) {
                is File -> {
                    Toast.makeText(this, item.filePath, Toast.LENGTH_SHORT).show()
                }
                is Book -> {
                    val intent = Intent(Intent.ACTION_VIEW, item.url.toUri())

                    val packageManager = packageManager
                    val activities = packageManager.queryIntentActivities(intent, 0)
                    val isIntentSafe = activities.isNotEmpty()

                    if (isIntentSafe)
                        startActivity(intent)
                    else
                        Toast.makeText(this, "해당 URL을 열 수 있는 앱이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        qualificationViewModel.apply {
            qualificationDetails.observe(this@QualificationDetailsActivity) { result ->
                binding.progressIndicator.hide()

                val items = mutableListOf<QualificationDetailsItem>()

                if (result.schedule.isNotEmpty()) {
                    items.add(QualificationDetailsItem(ItemType.HEADER, "시험 일정"))
                    items.addAll(result.schedule.map { QualificationDetailsItem(ItemType.SCHEDULE, it) })
                }

                if (result.fee.writtenFee != null || result.fee.practicalFee != null) {
                    items.add(QualificationDetailsItem(ItemType.HEADER, "수수료"))
                    items.add(QualificationDetailsItem(ItemType.FEE, result.fee))
                }

                result.tendency?.let {
                    items.add(QualificationDetailsItem(ItemType.HEADER, "출제 경향"))
                    items.add(QualificationDetailsItem(ItemType.TENDENCY, it))
                }

                if (result.standard.isNotEmpty()) {
                    items.add(QualificationDetailsItem(ItemType.HEADER, "출제 기준"))
                    items.addAll(result.standard.map { QualificationDetailsItem(ItemType.STANDARD, it) })
                }

                if (result.question.isNotEmpty()) {
                    items.add(QualificationDetailsItem(ItemType.HEADER, "공개 문제"))
                    items.addAll(result.question.map { QualificationDetailsItem(ItemType.QUESTION, it) })
                }

                result.acquisition?.let {
                    items.add(QualificationDetailsItem(ItemType.HEADER, "취득 방법"))
                    items.add(QualificationDetailsItem(ItemType.ACQUISITION, it))
                }

                if (result.books.isNotEmpty()) {
                    items.add(QualificationDetailsItem(ItemType.HEADER, "추천 도서"))
                    items.addAll(result.books.map { QualificationDetailsItem(ItemType.BOOK, it) })
                }

                qualificationDetailsAdapter.setItemList(items)
            }

            errorMessage.observe(this@QualificationDetailsActivity) { message ->
                message.getContentIfNotHandled()?.let { Toast.makeText(this@QualificationDetailsActivity, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }
}