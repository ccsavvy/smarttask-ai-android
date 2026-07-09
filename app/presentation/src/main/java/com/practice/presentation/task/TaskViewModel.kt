package com.practice.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practice.domain.usecase.AddTaskUseCase
import com.practice.domain.usecase.GetTaskSuggestionsUseCase
import com.practice.domain.usecase.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val getTaskSuggestionsUseCase: GetTaskSuggestionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTasksUseCase().collect { tasks ->
                _uiState.update { it.copy(tasks = tasks) }
            }
        }
    }

    fun onNewTaskTitleChange(title: String) {
        _uiState.update { it.copy(newTaskTitle = title) }
    }

    fun onGoalInputChange(goal: String) {
        _uiState.update { it.copy(goalInput = goal, suggestionError = null) }
    }

    fun addTask() {
        val title = _uiState.value.newTaskTitle.trim()
        if (title.isEmpty()) return

        viewModelScope.launch {
            addTaskUseCase(title)
            _uiState.update { it.copy(newTaskTitle = "") }
        }
    }

    fun addSuggestedTask(title: String) {
        viewModelScope.launch {
            addTaskUseCase(title)
        }
    }

    fun requestSuggestions() {
        val goal = _uiState.value.goalInput.trim()
        if (goal.isEmpty()) {
            _uiState.update { it.copy(suggestionError = "Enter a goal to get suggestions") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingSuggestions = true,
                    suggestionError = null,
                    suggestions = emptyList()
                )
            }

            runCatching {
                getTaskSuggestionsUseCase(goal)
            }.onSuccess { suggestions ->
                _uiState.update {
                    it.copy(
                        isLoadingSuggestions = false,
                        suggestions = suggestions,
                        suggestionError = if (suggestions.isEmpty()) {
                            "No suggestions available"
                        } else {
                            null
                        }
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingSuggestions = false,
                        suggestionError = error.message ?: "Failed to load suggestions"
                    )
                }
            }
        }
    }
}
