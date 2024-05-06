package com.credential.cubrism.view
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.ActivityMypageCertmanageBinding
import com.credential.cubrism.view.adapter.MyPageCertAdapter
import com.credential.cubrism.viewmodel.CertListViewModel

class MyPageCertManageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMypageCertmanageBinding.inflate(layoutInflater) }
    private lateinit var certListViewModel: CertListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        certListViewModel = ViewModelProvider(this)[CertListViewModel::class.java]
        setupView()
        setupRecyclerView()
    }

    private fun setupView() {

        // 뒤로가기
        binding.backBtn.setOnClickListener { finish() }

        // 설정하기 버튼
        binding.btnApply.setOnClickListener {
            Toast.makeText(this, "변경 사항을 저장했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRecyclerView() : MyPageCertAdapter {

        val items = certListViewModel.certList.value ?: ArrayList()
        val adapter = MyPageCertAdapter(items)

        binding.recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        return adapter
    }
}