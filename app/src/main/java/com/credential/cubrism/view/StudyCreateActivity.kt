package com.credential.cubrism.view

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.credential.cubrism.databinding.ActivityAddstudyBinding
import com.credential.cubrism.view.adapter.StudyGroupTagAdapter2
import com.credential.cubrism.view.adapter.TagDeleteClickListener
import com.credential.cubrism.view.utils.ItemDecoratorDivider

class StudyCreateActivity : AppCompatActivity(), TagDeleteClickListener {
    private val binding by lazy { ActivityAddstudyBinding.inflate(layoutInflater) }

    private lateinit var studyGroupTagAdapter2 : StudyGroupTagAdapter2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupView()
    }

    override fun onDeleteClick(position: Int) {
        studyGroupTagAdapter2.deleteItem(position)
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        studyGroupTagAdapter2 = StudyGroupTagAdapter2(this)

        binding.recyclerView.apply {
            adapter = studyGroupTagAdapter2
            addItemDecoration(ItemDecoratorDivider(0, 20, 0, 20, 0, 0, 0))
        }
    }

    private fun setupView() {
        binding.btnAddTag.setOnClickListener {
            binding.editTag.text?.let {
                if (it.isNotEmpty()) {
                    studyGroupTagAdapter2.addItem(it.toString())
                    binding.editTag.text?.clear()
                }
            }
        }

        binding.editMember.formatNumber()
    }

    // 인원수를 0으로 시작하는 것을 방지
    private fun EditText.formatNumber() {
        addTextChangedListener { text ->
            if (text.toString().startsWith("0"))
                setText(getText().toString().substring(1))
        }
    }

//    private fun submitButtonPressed() { // 가입 하기 버튼 눌렀을 때 처리 하는 함수
//        val numS = binding.numS.text.toString()
//
//        val numSInt = if (numS.isNotEmpty()) {
//            numS.toInt()
//        } else 0 // 기본 값은 0으로 설정
//
//        when {
//            (binding.editGroupName.text.isEmpty()) ->
//                Toast.makeText(this, "그룹 명을 입력하세요.", Toast.LENGTH_SHORT).show()
//
//            (numS.isEmpty()) -> Toast.makeText(this, "스터디 그룹 인원 수를 입력하세요.", Toast.LENGTH_SHORT).show()
//            (numSInt <= 1) -> Toast.makeText(this, "스터디 그룹 인원 수를 2명 이상 입력하세요.", Toast.LENGTH_SHORT).show()
//
//            (binding.editInfo.text.isEmpty()) ->
//                Toast.makeText(this, "스터디 그룹 설명을 입력하세요.", Toast.LENGTH_SHORT).show()
//
//            else -> {
//                hideKeyboard()
//                Toast.makeText(this, "스터디를 등록하였습니다.", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }
//
//    // 뷰에 포커스를 주고 키보드를 숨기는 함수
//    private fun hideKeyboard() {
//        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        currentFocus?.let { view ->
//            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//        }
//    }
}