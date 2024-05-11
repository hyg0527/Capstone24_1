package com.credential.cubrism.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.credential.cubrism.databinding.DialogAddGoalBinding

class GoalAddDialog(private val context: Context, private val onConfirm: (title: String) -> Unit) : DialogFragment() {
    private var _binding: DialogAddGoalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDialog() {
        dialog?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.attributes?.width = (context.resources.displayMetrics.widthPixels.times(0.9)).toInt()
        }
    }

    private fun setupView() {
        binding.txtCancel.setOnClickListener {
            dismiss()
        }

        binding.txtConfirm.setOnClickListener {
            val title = binding.editTitle.text.toString()

            if (title.isEmpty()) {
                Toast.makeText(context, "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onConfirm(binding.editTitle.text.toString())
            dismiss()
        }
    }
}