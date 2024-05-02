package com.credential.cubrism.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMypageCertmanageBinding

class MyPageCertManageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMypageCertmanageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val layouts = listOf(binding.layoutCert1, binding.layoutCert2, binding.layoutCert3)
        val deleteButtons = listOf(binding.btnDelete1, binding.btnDelete2, binding.btnDelete3)

        binding.backBtn.setOnClickListener { finish() }
        binding.btnApply.setOnClickListener {
            Toast.makeText(this, "변경 사항을 저장했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        deleteButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                layouts[index].visibility = View.GONE
                if (layouts.all { it.visibility == View.GONE }) {
                    binding.noCert.visibility = View.VISIBLE
                }
            }
        }



    }
}