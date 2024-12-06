package com.example.studypath.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

//Source for foreign key
//https://stackoverflow.com/questions/49414476/using-rooms-foreignkey-as-entity-parameter-in-kotlin
//https://chatgpt.com/share/6751bfd9-4be8-8004-ad72-4c4928d9624e
@Entity(tableName = "subtasks",
    foreignKeys = [ForeignKey(entity = Task::class, parentColumns = ["taskId"], childColumns = ["taskId"], onDelete = CASCADE)],
    indices = [Index(value = ["taskId"])]
)
data class Subtasks(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int,
    val name: String,
    val completed: Boolean
)