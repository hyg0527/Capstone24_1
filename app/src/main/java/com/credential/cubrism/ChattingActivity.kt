package com.credential.cubrism

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
        val reply = findViewById<TextView>(R.id.replyUser)
        val replyImg = findViewById<ImageView>(R.id.replyIMG)

        val sendingBtn = findViewById<ImageButton>(R.id.sendingBtn) // 메시지 전송 로직
        sendingBtn.setOnClickListener {
            val sendingText = findViewById<EditText>(R.id.editTextSendMessage)
            if (sendingText.text.isNotEmpty()) {
                val sendTxt = sendingText.text.toString()
                val item: Chat

                if (reply.isVisible && replyImg.isVisible) {
                    item = Chat("user", R.drawable.profil_image, sendTxt,false, true)
                }
                else {
                    item = Chat("user", R.drawable.profil_image, sendTxt,false, false)
                }

                chatAdapter.addItem(item)
                sendingText.setText("")

                // 보내고나면 텍스트칸 초기화 로직
                reply.visibility = View.GONE
                replyImg.visibility = View.GONE
            }
            else {
                Toast.makeText(this, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }


        chatAdapter.setReplyListener(object: ReplyListener {
            override fun onClicked(isReply: Boolean) {
                if (isReply) {
                    reply.visibility = View.GONE
                    replyImg.visibility = View.GONE
                }
                else {
                    reply.visibility = View.VISIBLE
                    replyImg.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initChatList(): ChattingAdapter {
        val itemList = ArrayList<Chat>()
//            .apply {
//            for (i in 1..6) {
//                add(Chat("user", R.drawable.profil_image, "${i}번째 텍스트입니다.", false))
//                add(Chat("user", R.drawable.profil_image, "-${i}번째 텍스트입니다.", false))
//            }
//        }
        val recyclerView = findViewById<RecyclerView>(R.id.chattingView)
        val adapter = ChattingAdapter(itemList)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false

        return adapter
    }
}