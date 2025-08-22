    package com.example.to_do_app.data

    import android.util.Log
    import com.example.to_do_app.domain.TaskActivity
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.ListenerRegistration
    import com.google.firebase.firestore.Query
    import kotlinx.coroutines.tasks.await

    class TaskActivityRepo {
        private val firestore = FirebaseFirestore.getInstance()
        private val collection = firestore.collection("task_activities")

        suspend fun addActivity(activity: TaskActivity): Result<Unit> {
            return try {
                collection.document(activity.id).set(activity).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("TaskActivityRepo", "Failed to add activity: ", e)
                Result.failure(e)
            }
        }

        suspend fun getActivitiesByTaskId(taskId: String): List<TaskActivity> {
            return try {
                val snapshot = collection.whereEqualTo("taskId", taskId).get().await()
                snapshot.documents.mapNotNull { it.toObject(TaskActivity::class.java) }
            } catch (e: Exception) {
                Log.e("TaskActivityRepo", "Failed to get activities by taskId: ", e)
                emptyList()
            }
        }

        suspend fun getActivitiesByUserId(userId: String): List<TaskActivity> {
            return try {
                val snapshot = collection.whereEqualTo("userId", userId).get().await()
                snapshot.documents.mapNotNull { it.toObject(TaskActivity::class.java) }
            } catch (e: Exception) {
                Log.e("TaskActivityRepo", "Failed to get activities by userId: ", e)
                emptyList()
            }
        }

        suspend fun getTaskActivityByProjectId(projectId: String): List<TaskActivity> {
            return try {
                val snapshot = collection.whereEqualTo("projectId", projectId).get().await()
                snapshot.documents.mapNotNull { it.toObject(TaskActivity::class.java) }
            } catch (e: Exception) {
                Log.e("TaskActivityRepo", "Failed to get activities by projectId: ", e)
                emptyList()
            }
        }

        suspend fun getRecentActivities(limit: Long = 10): List<TaskActivity> {
            return try {
                val snapshot = collection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(limit)
                    .get().await()
                snapshot.documents.mapNotNull { it.toObject(TaskActivity::class.java) }
            } catch (e: Exception) {
                Log.e("TaskActivityRepo", "Failed to get recent activities: ", e)
                emptyList()
            }
        }

        suspend fun deleteActivity(activityId: String): Result<Unit> {
            return try {
                collection.document(activityId).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("TaskActivityRepo", "Failed to delete activity: ", e)
                Result.failure(e)
            }
        }

        fun listenActivitiesByUserId(userId: String, onChange: (List<TaskActivity>) -> Unit): ListenerRegistration {
            return collection
//                .whereEqualTo("userId", userId)
                .whereArrayContains("notifiedUserIds", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("TaskActivityRepo", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val activities = snapshot.documents.mapNotNull { it.toObject(TaskActivity::class.java) }
                        onChange(activities)
                    }
                }
        }

    }