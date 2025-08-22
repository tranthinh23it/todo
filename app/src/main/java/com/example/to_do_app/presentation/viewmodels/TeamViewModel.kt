package com.example.to_do_app.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_do_app.data.TeamRepo
import com.example.to_do_app.domain.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamViewModel : ViewModel() {
    private val repo = TeamRepo()

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    private val _selectedTeam = MutableStateFlow<Team?>(null)
    val selectedTeam: StateFlow<Team?> = _selectedTeam

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAllTeams() {
        viewModelScope.launch {
            val result = repo.getAllTeams()
            _teams.value = result
        }
    }

    fun getTeamById(teamId: String) {
        viewModelScope.launch {
            Log.d("TeamViewModel", "Fetching team with ID: $teamId")
            val team = repo.getTeamById(teamId)
            _selectedTeam.value = team
        }
    }

    fun getTeamsByUserId(userId: String) {
        viewModelScope.launch {
            val result = repo.getTeamsByUserId(userId)
            _teams.value = result
        }
    }

    fun addTeam(team: Team) {
        viewModelScope.launch {
            val result = repo.addTeam(team)
            if (result.isSuccess) {
                loadAllTeams()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun updateTeam(team: Team) {
        viewModelScope.launch {
            val result = repo.updateTeam(team)
            if (result.isSuccess) {
                loadAllTeams()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun deleteTeam(teamId: String) {
        viewModelScope.launch {
            val result = repo.deleteTeam(teamId)
            if (result.isSuccess) {
                loadAllTeams()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 