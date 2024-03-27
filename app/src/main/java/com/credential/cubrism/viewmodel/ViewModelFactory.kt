package com.credential.cubrism.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.model.repository.StudyGroupRepository

class ViewModelFactory(private val repository: Any) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(repository as AuthRepository) as T
            QualificationViewModel::class.java -> QualificationViewModel(repository as QualificationRepository) as T
            StudyGroupViewModel::class.java -> StudyGroupViewModel(repository as StudyGroupRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}