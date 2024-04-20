package com.credential.cubrism.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.viewpager2.widget.ViewPager2
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityPhotoViewBinding
import com.credential.cubrism.view.adapter.PhotoAdapter

class PhotoViewActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPhotoViewBinding.inflate(layoutInflater) }

    private val photoAdapter = PhotoAdapter()

    private val imageUrl by lazy { intent.getStringArrayListExtra("url") }
    private val position by lazy { intent.getIntExtra("position", 0) }
    private val download by lazy { intent.getBooleanExtra("download", false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        if (download) {
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.photo_download_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.download -> {
                            imageUrl?.let {
                                val url = it[binding.viewPager.currentItem]
                                Toast.makeText(this@PhotoViewActivity, url, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    return false
                }
            })
        }
    }

    private fun setupViewPager() {
        imageUrl?.let {
            binding.viewPager.apply {
                adapter = photoAdapter
                photoAdapter.setItemList(it)
                offscreenPageLimit = it.size
                setCurrentItem(position, false)
            }
            binding.txtCount.text = "${position + 1} / ${imageUrl?.size}"
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.txtCount.text = "${position + 1} / ${imageUrl?.size}"
            }
        })
    }
}