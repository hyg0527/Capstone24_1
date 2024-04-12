package com.credential.cubrism.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.credential.cubrism.databinding.DialogCommentBinding
import com.credential.cubrism.model.dto.Comments
import com.credential.cubrism.view.adapter.CommentDialogAdapter
import com.credential.cubrism.viewmodel.PostViewModel

class CommentDialog(private val comment: Comments) : DialogFragment() {
    private var _binding: DialogCommentBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by activityViewModels()

    private val commentDialogAdapter = CommentDialogAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCommentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDialog() {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.width = (context.resources.displayMetrics.widthPixels.times(0.55)).toInt()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = commentDialogAdapter
            setHasFixedSize(true)
        }

        commentDialogAdapter.apply {
            setItemList(listOf("수정", "삭제"))
            setOnItemClickListener { item, _ ->
                postViewModel.setClickedItem(Pair(comment, item))
                dismiss()
            }
        }
    }
}