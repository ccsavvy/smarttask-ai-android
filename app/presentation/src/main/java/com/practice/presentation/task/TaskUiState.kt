package com.practice.presentation.task

import com.practice.domain.model.Task

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val newTaskTitle: String = "",
    val goalInput: String = "",
    val suggestions: List<String> = emptyList(),
    val isLoadingSuggestions: Boolean = false,
    val suggestionError: String? = null
)
