package com.credential.cubrism.view

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private var fileUrl: String? = null

    companion object {
        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    fileUrl?.let { downloadFile(it) }
                } else {
                    Toast.makeText(this, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
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
                    fileUrl = "https://www.q-net.or.kr/crf011.do?id=crf01106&gSite=Q&gId=&filePath=${item.filePath}&fileName=${item.fileName}"
                    Log.d("테스트", "fileUrl: $fileUrl")
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        checkStoragePermission()
                    } else {
                        fileUrl?.let { downloadFile(it) }
                    }
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

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        } else {
            fileUrl?.let { downloadFile(it) }
        }
    }

    private fun downloadFile(url: String) {
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
        return Uri.parse(url).getQueryParameter("fileName") ?: ""
    }

    // 파일 확장자 설정
    private fun determineMimeType(url: String): String {
        val fileName = Uri.parse(url).getQueryParameter("fileName")
        val format = fileName?.substringAfterLast(".")?.lowercase()

        return when (format) {
            "pdf" -> "application/pdf"
            "hwp" -> "application/x-hwp"
            "hwpx" -> "application/haansofthwpx"
            "zip" -> "application/zip"
            else -> ""
        }
    }

    // 다운로드 매니저로 파일 다운로드
    private fun enqueueDownload(url: String, fileName: String, mimeType: String) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("파일 다운로드 중...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setMimeType(mimeType)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val file = java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            request.setDestinationUri(Uri.fromFile(file))
        } else {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }

        downloadManager.enqueue(request)
    }
}