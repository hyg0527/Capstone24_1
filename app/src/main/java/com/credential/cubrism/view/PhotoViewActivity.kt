package com.credential.cubrism.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.credential.cubrism.databinding.ActivityPhotoViewBinding

class PhotoViewActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPhotoViewBinding.inflate(layoutInflater) }

    private val imageUrl by lazy { intent.getStringExtra("imageUrl") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupView() {
        imageUrl?.let {
            Glide.with(this)
                .load(it)
                .placeholder(ColorDrawable(Color.TRANSPARENT))
                .into(binding.imgPhoto)
        }
    }
}