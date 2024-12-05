package com.example.studypath.config

import androidx.room.TypeConverter
import com.example.studypath.model.Subtasks
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SubtaskConverter {

    @TypeConverter
    fun fromSubtasksList(subtask: List<Subtasks>?): String {
        return Gson().toJson(subtask)
    }

    @TypeConverter
    fun toSubtasksList(subtasksJson: String): List<Subtasks> {
        val type = object : TypeToken<List<Subtasks>>() {}.type
        return Gson().fromJson(subtasksJson, type)
        //return Gson().fromJson(subtasksJson, Array<Subtasks>::class.java).toList()
    }
}