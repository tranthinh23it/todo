package com.example.to_do_app.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_do_app.data.TaskRepo
import com.example.to_do_app.domain.Task
import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repo = TaskRepo()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks
    
    init {
        Log.d("TaskViewModel", "TaskViewModel initialized, initial tasks size: ${_tasks.value.size}")
    }

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAllTasks() {
        viewModelScope.launch {
            val result = repo.getAllTasks()
            _tasks.value = result
        }
    }

    fun getTaskById(taskId: String) {
        viewModelScope.launch {
            val task = repo.getTaskById(taskId)
            _selectedTask.value = task
        }
    }

    fun getTasksByUserId(userId: String) {
        if (userId.isBlank()) {
//            Log.w("TaskViewModel", "Skip getTasksByUserId: blank userId")
            return
        }
//        Log.d("TaskViewModel", "getTasksByUserId called with userId: $userId")
        viewModelScope.launch {
            try {
                val result = repo.getTasksByUserId(userId)
//                Log.d("TaskViewModel", "Received ${result.size} tasks from repo")
                _tasks.value = result
            } catch (e: Exception) {
//                Log.e("TaskViewModel", "Error getting tasks by userId: $userId", e)
                _error.value = e.message
            }
        }
    }


    fun getTasksByProjectId(projectId: String) {
        viewModelScope.launch {
            val result = repo.getTasksByProjectId(projectId)
            _tasks.value = result
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            val result = repo.addTask(task)
            if (result.isSuccess) {
                loadAllTasks()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val result = repo.updateTask(task)
            if (result.isSuccess) {
                loadAllTasks()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            Log.d("Log call back" ,"deleteTask $taskId success ")
            val result = repo.deleteTask(taskId)
            if (result.isSuccess) {
                loadAllTasks()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
    
    // Method để tạo test data
    fun createTestTask() {
        viewModelScope.launch {
            val testTask = Task(
                id = "test_${System.currentTimeMillis()}",
                title = "Test Task",
                description = "This is a test task",
                project = "test_project",
                assignee = listOf("uFn2a1izcMOmQ6V61tVqRraZm823"),
                creator = "uFn2a1izcMOmQ6V61tVqRraZm823",
                status = TaskStatus.IN_PROGRESS,
                priority = TaskPriority.HIGH,
                dateStart = "2024-01-15",
                dateDue = "2024-01-20"
            )
            addTask(testTask)
        }
    }
} 