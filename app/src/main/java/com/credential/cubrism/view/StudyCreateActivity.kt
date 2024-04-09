package com.credential.cubrism.view

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityAddstudyBinding

class StudyCreateActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddstudyBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            btnBack.setOnClickListener { // 뒤로 가기 버튼
                finish()
            }
            btnCreate.setOnClickListener { // 스터디 등록 부분
                hideKeyboard()
                submitButtonPressed()
            }
        }

    }

    private fun submitButtonPressed() { // 가입 하기 버튼 눌렀을 때 처리 하는 함수
        val numS = binding.numS.text.toString()

        val numSInt = if (numS.isNotEmpty()) {
            numS.toInt()
        } else 0 // 기본 값은 0으로 설정

        when {
            (binding.editGroupName.text.isEmpty()) ->
                Toast.makeText(this, "그룹 명을 입력하세요.", Toast.LENGTH_SHORT).show()

            (numS.isEmpty()) -> Toast.makeText(this, "스터디 그룹 인원 수를 입력하세요.", Toast.LENGTH_SHORT).show()
            (numSInt <= 1) -> Toast.makeText(this, "스터디 그룹 인원 수를 2명 이상 입력하세요.", Toast.LENGTH_SHORT).show()

            (binding.editInfo.text.isEmpty()) ->
                Toast.makeText(this, "스터디 그룹 설명을 입력하세요.", Toast.LENGTH_SHORT).show()

            else -> {
                hideKeyboard()
                Toast.makeText(this, "스터디를 등록하였습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // 뷰에 포커스를 주고 키보드를 숨기는 함수
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { view ->
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}