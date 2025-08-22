package com.example.to_do_app.data

import android.util.Log
import com.example.to_do_app.domain.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class TaskRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("tasks")

    suspend fun addTask(task: Task): Result<String> {
        return try {
            val docRef = collection.add(task.copy(id = "")).await() // thêm, Firestore tự tạo id
            val id = docRef.id
            docRef.update("id", id).await()                          // cập nhật field id
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            collection.document(task.id).set(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to update task: ", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            collection.document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to delete task: ", e)
            Result.failure(e)
        }
    }

    suspend fun getTaskById(taskId: String): Task? {
        return try {
            val doc = collection.document(taskId).get().await()
            doc.toObject(Task::class.java)
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to get task by id: ", e)
            null
        }
    }

    suspend fun getAllTasks(): List<Task> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to get all tasks: ", e)
            emptyList()
        }
    }
    suspend fun getTasksByUserId(userId: String): List<Task> = try {
//        Log.d("TaskRepo", "Fetching tasks for userId: $userId")

        coroutineScope {
            val assigneeDeferred = async {
                collection.whereArrayContains("assignee", userId).get().await()
            }
            val creatorDeferred = async {
                collection.whereEqualTo("creator", userId).get().await()
            }

            val assigneeDocs = assigneeDeferred.await().documents
            val creatorDocs  = creatorDeferred.await().documents

            fun mapDocs(docs: List<DocumentSnapshot>): List<Task> =
                docs.mapNotNull { doc ->
                    doc.toObject(Task::class.java)?.let { t ->
                        if (t.id.isBlank()) t.copy(id = doc.id) else t
                    }
                }

            val tasksAssignee = mapDocs(assigneeDocs)
            val tasksCreator  = mapDocs(creatorDocs)

//            Log.d("TaskRepo", "Assignee tasks: ${tasksAssignee.size}, Creator tasks: ${tasksCreator.size}")

            // Merge, ưu tiên theo id (LinkedHashMap để giữ thứ tự xuất hiện)
            val merged = LinkedHashMap<String, Task>()
            (tasksAssignee + tasksCreator).forEach { t ->
                val key = if (t.id.isBlank()) "missing_${t.title}_${t.dateStart}_${t.hashCode()}" else t.id
                merged.putIfAbsent(key, t)
            }

            val all = merged.values.toList()
//            Log.d("TaskRepo", "Total unique tasks: ${all.size}")
            all
        }
    } catch (e: Exception) {
        Log.e("TaskRepo", "Failed to get tasks by userId: $userId", e)
        emptyList()
    }



    suspend fun getTasksByProjectId(projectId: String): List<Task> {
        return try {
            val snapshot = collection.whereEqualTo("project", projectId).get().await()
            snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to get tasks by projectId: ", e)
            emptyList()
        }
    }
} 