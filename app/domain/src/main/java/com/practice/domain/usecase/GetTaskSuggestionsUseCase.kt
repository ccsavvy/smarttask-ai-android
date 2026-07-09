package com.practice.domain.usecase

import com.practice.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskSuggestionsUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(goal: String): List<String> {
        require(goal.isNotBlank()) { "Goal cannot be empty" }
        return repository.getSuggestions(goal.trim())
    }
}
