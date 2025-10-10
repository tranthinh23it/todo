package com.example.to_do_app.data

import android.util.Log
import com.example.to_do_app.domain.Project
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProjectRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("projects")

    suspend fun addProject(project: Project): Result<Unit> {
        return try {
            // Nếu id trống thì tạo mới
            val docRef = if (project.id.isBlank()) {
                collection.document() // Firestore tạo ID mới
            } else {
                collection.document(project.id)
            }

            val projectWithId = project.copy(id = docRef.id)

            docRef.set(projectWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProjectRepo", "Failed to add project: ", e)
            Result.failure(e)
        }
    }

    suspend fun updateProject(project: Project): Result<Unit> {
        return try {
            collection.document(project.id).set(project).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProjectRepo", "Failed to update project: ", e)
            Result.failure(e)
        }
    }

    suspend fun deleteProject(projectId: String): Result<Unit> {
        return try {
            collection.document(projectId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProjectRepo", "Failed to delete project: ", e)
            Result.failure(e)
        }
    }

    suspend fun getProjectById(projectId: String): Project? {
        return try {
            val doc = collection.document(projectId).get().await()
            doc.toObject(Project::class.java)
        } catch (e: Exception) {
            Log.e("ProjectRepo", "Failed to get project by id: ", e)
            null
        }
    }

    suspend fun getAllProjects(): List<Project> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Project::class.java) }
        } catch (e: Exception) {
            Log.e("ProjectRepo", "Failed to get all projects: ", e)
            emptyList()
        }
    }

    suspend fun getProjectsByUserId(userId: String): List<Project> {
        return try {
            val snapshot = collection.whereArrayContains("members", userId).get().await()
            snapshot.documents.mapNotNull { it.toObject(Project::class.java) }
        } catch (e: Exception) {
            Log.e("ProjectRepo", "Failed to get projects by userId: ", e)
            emptyList()
        }
    }

    suspend fun getProjectsByTeamId(teamId: String): List<Project> {
        return try {
            val snapshot = collection.whereEqualTo("team", teamId).get().await()
            snapshot.documents.mapNotNull { it.toObject(Project::class.java) }
        } catch (e: Exception) {
            Log.e("ProjectRepo", "Failed to get projects by teamId: ", e)
            emptyList()
        }
    }


} 