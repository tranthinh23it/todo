package com.example.to_do_app.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_do_app.data.ProjectRepo
import com.example.to_do_app.domain.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {
    private val repo = ProjectRepo()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _selectedProject = MutableStateFlow<Project?>(null)
    val selectedProject: StateFlow<Project?> = _selectedProject

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAllProjects() {
        viewModelScope.launch {
            val result = repo.getAllProjects()
            _projects.value = result
        }
    }

    fun getProjectById(projectId: String) {
        viewModelScope.launch {
            val project = repo.getProjectById(projectId)
            _selectedProject.value = project
        }
    }

    fun getProjectsByUserId(userId: String) {
        viewModelScope.launch {
            val result = repo.getProjectsByUserId(userId)
            _projects.value = result
        }
    }

    fun getProjectsByTeamId(teamId: String) {
        viewModelScope.launch {
            Log.d("getProjectsByTeamId", "teamId: $teamId")
            val result = repo.getProjectsByTeamId(teamId)
            _projects.value = result
        }
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            val result = repo.addProject(project)
            if (result.isSuccess) {
                loadAllProjects()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            val result = repo.updateProject(project)
            if (result.isSuccess) {
                loadAllProjects()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            val result = repo.deleteProject(projectId)
            if (result.isSuccess) {
                loadAllProjects()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 