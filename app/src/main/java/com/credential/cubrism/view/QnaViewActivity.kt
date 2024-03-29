package com.credential.cubrism.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityQnaViewBinding

class QnaViewActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQnaViewBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

//        val receivedData = intent?.getParcelableExtra<QnaData>("qnaInfo")
//
//        val medalName = findViewById<Button>(R.id.txtPostingCategory)
//        val title = findViewById<TextView>(R.id.txtStudyInfoTitle)
//        val info = findViewById<TextView>(R.id.txtStudyInfoInfo)
//        val userName = findViewById<TextView>(R.id.textView33)
//
//        medalName.setText(receivedData?.medalName)
//        title.text = receivedData?.title
//        info.text = receivedData?.postIn
//        userName.text = receivedData?.userName
//        // 시간 필드 추가 요망
//
//        val chatAdapter = initChatList()
//        val reply = findViewById<TextView>(R.id.replyUser)
//        val replyImg = findViewById<ImageView>(R.id.replyIMG)
//
//        val sendingBtn = findViewById<ImageButton>(R.id.sendingBtn) // 메시지 전송 로직
//        sendingBtn.setOnClickListener {
//            val sendingText = findViewById<EditText>(R.id.editTextSendMessage)
//            if (sendingText.text.isNotEmpty()) {
//                val sendTxt = sendingText.text.toString()
//                val item: Chat
//
//                if (reply.isVisible && replyImg.isVisible) {
//                    item = Chat("user", R.drawable.reply_btn, sendTxt,false, true)
//                }
//                else {
//                    item = Chat("user", R.drawable.reply_btn, sendTxt,false, false)
//                }
//
//                chatAdapter.addItem(item)
//                sendingText.setText("")
//
//                // 보내고나면 텍스트칸 초기화 로직
//                reply.visibility = View.GONE
//                replyImg.visibility = View.GONE
//            }
//            else {
//                Toast.makeText(this, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//        chatAdapter.setReplyListener(object: ReplyListener {
//            override fun onClicked(isReply: Boolean) {
//                if (isReply) {
//                    reply.visibility = View.GONE
//                    replyImg.visibility = View.GONE
//                }
//                else {
//                    reply.visibility = View.VISIBLE
//                    replyImg.visibility = View.VISIBLE
//                }
//            }
//        })
//    }
//
//    private fun initChatList(): ChattingAdapter {
//        val itemList = ArrayList<Chat>()
////            .apply {
////            for (i in 1..6) {
////                add(Chat("user", R.drawable.profil_image, "${i}번째 텍스트입니다.", false))
////                add(Chat("user", R.drawable.profil_image, "-${i}번째 텍스트입니다.", false))
////            }
////        }
//        val recyclerView = findViewById<RecyclerView>(R.id.chattingView)
//        val adapter = ChattingAdapter(itemList)
//
//        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        recyclerView.adapter = adapter
//        recyclerView.isNestedScrollingEnabled = false
//
//        return adapter
    }
}