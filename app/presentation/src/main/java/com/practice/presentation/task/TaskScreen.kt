package com.practice.presentation.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TaskScreenContent(
        uiState = uiState,
        onNewTaskTitleChange = viewModel::onNewTaskTitleChange,
        onGoalInputChange = viewModel::onGoalInputChange,
        onAddTask = viewModel::addTask,
        onRequestSuggestions = viewModel::requestSuggestions,
        onAddSuggestedTask = viewModel::addSuggestedTask,
        modifier = modifier
    )
}

@Composable
fun TaskScreenContent(
    uiState: TaskUiState,
    onNewTaskTitleChange: (String) -> Unit,
    onGoalInputChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onRequestSuggestions: () -> Unit,
    onAddSuggestedTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "SmartTask AI",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            AiSuggestionSection(
                goalInput = uiState.goalInput,
                suggestions = uiState.suggestions,
                isLoading = uiState.isLoadingSuggestions,
                error = uiState.suggestionError,
                onGoalInputChange = onGoalInputChange,
                onRequestSuggestions = onRequestSuggestions,
                onAddSuggestedTask = onAddSuggestedTask
            )
        }

        item {
            HorizontalDivider()
        }

        item {
            Text(
                text = "Your Tasks",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.newTaskTitle,
                    onValueChange = onNewTaskTitleChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("New task") },
                    singleLine = true
                )
                Button(onClick = onAddTask) {
                    Text("Add")
                }
            }
        }

        if (uiState.tasks.isEmpty()) {
            item {
                Text(
                    text = "No tasks yet. Add one manually or use AI suggestions above.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(uiState.tasks, key = { it.id }) { task ->
                TaskItem(title = task.title, completed = task.completed)
            }
        }
    }
}

@Composable
private fun AiSuggestionSection(
    goalInput: String,
    suggestions: List<String>,
    isLoading: Boolean,
    error: String?,
    onGoalInputChange: (String) -> Unit,
    onRequestSuggestions: () -> Unit,
    onAddSuggestedTask: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "AI Task Suggestions",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Suggestions are based on your goal and past tasks stored in Room.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = goalInput,
                onValueChange = onGoalInputChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("What do you want to accomplish?") },
                singleLine = true
            )

            Button(
                onClick = onRequestSuggestions,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Suggest Tasks")
                }
            }

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            suggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = suggestion,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(onClick = { onAddSuggestedTask(suggestion) }) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(title: String, completed: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (completed) "✓ $title" else title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
