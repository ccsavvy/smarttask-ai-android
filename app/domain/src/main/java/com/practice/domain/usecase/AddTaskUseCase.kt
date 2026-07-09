package com.practice.domain.usecase

import com.practice.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(title: String) {
        repository.addTask(title.trim())
    }
}
