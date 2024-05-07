package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityMypageCertmanageBinding
import com.credential.cubrism.model.dto.FavoriteListDto
import com.credential.cubrism.model.repository.FavoriteRepository
import com.credential.cubrism.view.adapter.FavoriteAdapter
import com.credential.cubrism.view.adapter.FavoriteDeleteButtonClickListener
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.FavoriteViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class MyPageCertManageActivity : AppCompatActivity(), FavoriteDeleteButtonClickListener {
    private val binding by lazy { ActivityMypageCertmanageBinding.inflate(layoutInflater) }
    private val favoriteViewModel: FavoriteViewModel by viewModels { ViewModelFactory(FavoriteRepository()) }

    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        favoriteViewModel.getFavoriteList()
    }

    override fun onButtonClick(item: FavoriteListDto) {
        AlertDialog.Builder(this).apply {
            setTitle(item.name)
            setMessage("관심 자격증을 삭제하시겠습니까?")
            setNegativeButton("취소", null)
            setPositiveButton("삭제") { _, _ ->
                favoriteViewModel.deleteFavorite(item.favoriteId)
            }
            show()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter(this)

        binding.recyclerView.apply {
            adapter = favoriteAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 80, 0, 0, 0, 0, null))
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        favoriteViewModel.apply {
            favoriteList.observe(this@MyPageCertManageActivity) {
                binding.progressIndicator.hide()
                if (it.isEmpty())
                    binding.txtNoFavorite.visibility = android.view.View.VISIBLE
                else
                    binding.txtNoFavorite.visibility = android.view.View.GONE
                favoriteAdapter.setItemList(it)
            }

            deleteFavorite.observe(this@MyPageCertManageActivity) {
                Toast.makeText(this@MyPageCertManageActivity, it.message, Toast.LENGTH_SHORT).show()
                favoriteViewModel.getFavoriteList()
            }

            errorMessage.observe(this@MyPageCertManageActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@MyPageCertManageActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}