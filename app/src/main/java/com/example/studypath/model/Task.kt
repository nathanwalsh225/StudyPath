package com.example.studypath.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "tasks",
    foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["userId"], onDelete = CASCADE)],
    indices = [Index(value = ["userId"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,
    val userId: Int,
    val name: String,
    val description: String,
    val dueDate: String,
    val priority: Int, //Priority is an integer from | 1 - Low | 2 - Medium | 3 - High |
    val subtasks: List<Subtasks>
)