package com.credential.cubrism

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChattingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qna_viewpost)

        val backBtn_qnaview = findViewById<ImageButton>(R.id.backBtn_qnaview)
        backBtn_qnaview.setOnClickListener {
            finish()
        }

        val receivedData = intent?.getParcelableExtra<QnaData>("qnaInfo")

        val medalName = findViewById<Button>(R.id.button2)
        val title = findViewById<TextView>(R.id.textView36)
        val info = findViewById<TextView>(R.id.textView37)
        val userName = findViewById<TextView>(R.id.textView33)

        medalName.setText(receivedData?.medalName)
        title.text = receivedData?.title
        info.text = receivedData?.postIn
        userName.text = receivedData?.userName
        // 시간 필드 추가 요망

        val chatAdapter = initChatList()

        val sendingBtn = findViewById<ImageButton>(R.id.sendingBtn) // 메시지 전송 로직
        sendingBtn.setOnClickListener {
            val sendingText = findViewById<EditText>(R.id.editTextSendMessage).text.toString()
            chatAdapter.addItem(sendingText)
        }
    }

    private fun initChatList(): ChattingAdapter {
        val itemList = ArrayList<String>().apply {
            for (i in 1..6) {
                add("${i}번째 텍스트입니다.")
                add("-${i}번째 텍스트입니다.")
            }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.chattingView)
        val adapter = ChattingAdapter(itemList)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        return adapter
    }
}