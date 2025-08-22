    package com.example.to_do_app.presentation.viewmodels

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.to_do_app.data.TaskActivityRepo
    import com.example.to_do_app.data.TaskRepo
    import com.example.to_do_app.domain.Task
    import com.example.to_do_app.domain.TaskActivity
    import com.google.firebase.firestore.ListenerRegistration
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch

    class TaskActivityViewModel()
        : ViewModel() {
        private val repo = TaskActivityRepo()

        private val _activities = MutableStateFlow<List<TaskActivity>>(emptyList())
        val activities: StateFlow<List<TaskActivity>> = _activities

        private val _error = MutableStateFlow<String?>(null)
        val error: StateFlow<String?> = _error

        private var listenerRegistration: ListenerRegistration? = null

        fun loadRecentActivities(limit: Long = 10) {
            viewModelScope.launch {
                val result = repo.getRecentActivities(limit)
                _activities.value = result
            }
        }

        fun getActivitiesByTaskId(taskId: String) {
            viewModelScope.launch {
                val result = repo.getActivitiesByTaskId(taskId)
                _activities.value = result
            }
        }

        fun getActivitiesByUserId(userId: String) {
            viewModelScope.launch {
                val result = repo.getActivitiesByUserId(userId)
                _activities.value = result
            }
        }

        fun getTaskActivityByProjectId(projectId: String) {
            viewModelScope.launch {
                val result = repo.getTaskActivityByProjectId(projectId)
                _activities.value = result
            }
        }

        fun addActivity(activity: TaskActivity) {
            viewModelScope.launch {
                val result = repo.addActivity(activity)
                if (result.isSuccess) {
                    loadRecentActivities()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            }
        }

        fun deleteActivity(activityId: String) {
            viewModelScope.launch {
                val result = repo.deleteActivity(activityId)
                if (result.isSuccess) {
                    loadRecentActivities()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            }
        }

        fun clearError() {
            _error.value = null
        }


        fun observeActivitiesByUserId(userId: String) {
            listenerRegistration?.remove() // Gỡ listener cũ nếu có

            listenerRegistration = repo.listenActivitiesByUserId(userId) { activityList ->
                _activities.value = activityList
            }
        }
        override fun onCleared() {
            super.onCleared()
            listenerRegistration?.remove()
        }



    }