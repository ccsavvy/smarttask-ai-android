package com.practice.data

import com.practice.data.local.TaskDao
import com.practice.data.local.TaskEntity
import com.practice.data.mapper.toDomain
import com.practice.data.remote.ApiService
import com.practice.data.remote.SuggestRequest
import com.practice.domain.model.Task
import com.practice.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao,
    private val api: ApiService
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> {
        return dao.getTasks().map { entities -> entities.toDomain() }
    }

    override suspend fun addTask(title: String) {
        if (title.isBlank()) return
        dao.insertTask(TaskEntity(title = title, completed = false))
    }

    override suspend fun getSuggestions(goal: String): List<String> {
        val history = dao.getTaskTitles()
        return api.suggestTasks(SuggestRequest(goal = goal, history = history)).tasks
    }
}
