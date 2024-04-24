package com.credential.cubrism.view

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.viewpager2.widget.ViewPager2
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityPhotoViewBinding
import com.credential.cubrism.view.adapter.PhotoAdapter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoViewActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPhotoViewBinding.inflate(layoutInflater) }

    private val photoAdapter = PhotoAdapter()

    private val url by lazy { intent.getStringArrayListExtra("url") }
    private val position by lazy { intent.getIntExtra("position", 0) }
    private val download by lazy { intent.getBooleanExtra("download", false) }

    private var imageUrl: String? = null

    companion object {
        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    imageUrl?.let { downloadImage(it) }
                } else {
                    Toast.makeText(this, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
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
                            url?.let { images ->
                                imageUrl = images[binding.viewPager.currentItem].toString()
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                                    checkStoragePermission()
                                } else {
                                    imageUrl?.let { downloadImage(it) }
                                }
                            }
                        }
                    }
                    return false
                }
            })
        }
    }

    private fun setupViewPager() {
        url?.let {
            binding.viewPager.apply {
                adapter = photoAdapter
                photoAdapter.setItemList(it)
                offscreenPageLimit = it.size
                setCurrentItem(position, false)
            }
            binding.txtCount.text = "${position + 1} / ${url?.size}"
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.txtCount.text = "${position + 1} / ${url?.size}"
            }
        })
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_PERMISSION_CODE)
        } else {
            imageUrl?.let { downloadImage(it) }
        }
    }

    private fun downloadImage(url: String) {
        val fileName = createFileName(url)
        val mimeType = determineMimeType(url)

        try {
            enqueueDownload(url, fileName, mimeType)
            Toast.makeText(this, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // 현재 날짜 및 시간으로 파일 이름 설정
    private fun createFileName(url: String): String {
        val format = url.substringAfterLast(".")
        val date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "$date.$format"
    }

    // 파일 확장자 설정
    private fun determineMimeType(url: String): String {
        val format = url.substringAfterLast(".").lowercase()

        return when (format) {
            "jpg" -> "image/jpg"
            "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            "bmp" -> "image/bmp"
            "svg" -> "image/svg+xml"
            else -> ""
        }
    }

    // 다운로드 매니저로 이미지 다운로드
    private fun enqueueDownload(url: String, fileName: String, mimeType: String) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("이미지 다운로드 중...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setMimeType(mimeType)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            request.setDestinationUri(Uri.fromFile(file))
        } else {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }

        downloadManager.enqueue(request)
    }
}