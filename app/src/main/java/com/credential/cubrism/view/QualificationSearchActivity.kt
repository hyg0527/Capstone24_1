package com.credential.cubrism.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.credential.cubrism.databinding.ActivityQualificationSearchBinding
import com.credential.cubrism.model.dto.FavoriteAddDto
import com.credential.cubrism.model.repository.FavoriteRepository
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.view.adapter.QualificationAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.FavoriteViewModel
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QualificationSearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQualificationSearchBinding.inflate(layoutInflater) }

    private val qualificationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }
    private val favoriteViewModel: FavoriteViewModel by viewModels { ViewModelFactory(FavoriteRepository()) }

    private val qualificationAdapter = QualificationAdapter()

    private val imm by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }

    private val type by lazy { intent.getStringExtra("type") }

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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = qualificationAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 0, Color.parseColor("#D3D3D3")))
            setHasFixedSize(true)
        }

        qualificationAdapter.setOnItemClickListener { item, _ ->
            imm.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)
            when (type) {
                "search" -> {
                    val intent = Intent(this, QualificationDetailsActivity::class.java)
                    intent.putExtra("qualificationCode", item.code)
                    intent.putExtra("qualificationName", item.name)
                    startActivity(intent).also { finish() }
                }
                "favorite" -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(item.name)
                        setMessage("관심 자격증에 추가하시겠습니까?")
                        setNegativeButton("취소", null)
                        setPositiveButton("확인") { _, _ ->
                            favoriteViewModel.addFavorite(FavoriteAddDto(item.code))
                        }
                        show()
                    }
                }
            }
        }
    }

    private fun setupView() {
        imm.showSoftInput(binding.editSearch, InputMethodManager.SHOW_IMPLICIT)

        qualificationViewModel.getQualificationList()

        binding.editSearch.requestFocus()
        binding.editSearch.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                binding.btnClose.visibility = View.VISIBLE
            } else {
                binding.btnClose.visibility = View.INVISIBLE
            }
            qualificationAdapter.filter.filter(it.toString())
        }

        binding.btnClose.setOnClickListener {
            binding.editSearch.text?.clear()
        }
    }

    private fun observeViewModel() {
        qualificationViewModel.apply {
            qualificationList.observe(this@QualificationSearchActivity) { result ->
                qualificationAdapter.setItemList(result)
            }

            errorMessage.observe(this@QualificationSearchActivity) {
                it.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@QualificationSearchActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        favoriteViewModel.apply {
            addFavorite.observe(this@QualificationSearchActivity) {
                Toast.makeText(this@QualificationSearchActivity, it.message, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}