package com.practice.data.mapper

import com.practice.data.local.TaskEntity
import com.practice.domain.model.Task

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    completed = completed
)

fun List<TaskEntity>.toDomain(): List<Task> = map { it.toDomain() }
