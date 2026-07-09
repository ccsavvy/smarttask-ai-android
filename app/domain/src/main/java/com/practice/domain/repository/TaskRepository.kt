package com.practice.domain.repository

import com.practice.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun addTask(title: String)
    suspend fun getSuggestions(goal: String): List<String>
}
