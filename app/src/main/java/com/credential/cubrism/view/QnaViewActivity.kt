package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityQnaViewBinding
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QnaViewActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQnaViewBinding.inflate(layoutInflater) }

    private val viewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val postId = intent.getIntExtra("postId", -1)
        val boardName = intent.getStringExtra("boardName")

        if (postId != -1 && boardName != null) {
            viewModel.getPostView(boardName, postId)
        }

        viewModel.postView.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    val postView = result.data
                    Glide.with(this).load(postView.profileImageUrl).error(R.drawable.profil_image).into(binding.imgProfile)
                    binding.txtNickname.text = postView.nickname
                    binding.txtCategory.text = postView.category
                    binding.txtTitle.text = postView.title
                    binding.txtContent.text = postView.content.replace(" ", "\u00A0")
                }
                is ResultUtil.Error -> { Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
            }
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