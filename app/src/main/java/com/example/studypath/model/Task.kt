package com.example.studypath.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val dueDate: String,
    val priority: Int, //Priority is an integer from | 1 - Low | 2 - Medium | 3 - High |
    val subtasks: List<Subtasks>
)