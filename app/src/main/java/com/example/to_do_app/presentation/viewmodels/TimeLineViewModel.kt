package com.example.to_do_app.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_do_app.data.TimeLineRepo
import com.example.to_do_app.domain.TimeLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimeLineViewModel : ViewModel() {
    private val repo = TimeLineRepo()

    private val _timeLines = MutableStateFlow<List<TimeLine>>(emptyList())
    val timeLines: StateFlow<List<TimeLine>> = _timeLines

    private val _selectedTimeLine = MutableStateFlow<TimeLine?>(null)
    val selectedTimeLine: StateFlow<TimeLine?> = _selectedTimeLine

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAllTimeLines() {
        viewModelScope.launch {
            val result = repo.getAllTimeLines()
            _timeLines.value = result
        }
    }

    fun getTimeLineById(timeLineId: String) {
        viewModelScope.launch {
            val timeLine = repo.getTimeLineById(timeLineId)
            _selectedTimeLine.value = timeLine
        }
    }

    fun getTimeLinesByProjectId(projectId: String) {
        viewModelScope.launch {
            Log.d("TimeLineViewModel", "Getting timelines by projectId: $projectId")
            val result = repo.getTimeLinesByProjectId(projectId)
            _timeLines.value = result
        }
    }

    fun addTimeLine(timeLine: TimeLine) {
        viewModelScope.launch {
            val result = repo.addTimeLine(timeLine)
            if (result.isSuccess) {
                loadAllTimeLines()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun updateTimeLine(timeLine: TimeLine) {
        viewModelScope.launch {
            val result = repo.updateTimeLine(timeLine)
            if (result.isSuccess) {
                loadAllTimeLines()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun deleteTimeLine(timeLineId: String) {
        viewModelScope.launch {
            val result = repo.deleteTimeLine(timeLineId)
            if (result.isSuccess) {
                loadAllTimeLines()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}