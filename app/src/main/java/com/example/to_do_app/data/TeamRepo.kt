package com.example.to_do_app.data

import android.util.Log
import com.example.to_do_app.domain.Team
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TeamRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("teams")

    suspend fun addTeam(team: Team): Result<Unit> {
        return try {
            collection.document(team.id).set(team).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TeamRepo", "Failed to add team: ", e)
            Result.failure(e)
        }
    }

    suspend fun updateTeam(team: Team): Result<Unit> {
        return try {
            collection.document(team.id).set(team).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TeamRepo", "Failed to update team: ", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTeam(teamId: String): Result<Unit> {
        return try {
            collection.document(teamId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TeamRepo", "Failed to delete team: ", e)
            Result.failure(e)
        }
    }

    suspend fun getTeamById(teamId: String): Team? {
        return try {
            Log.d("TeamRepo", "getTeamById called with teamId: $teamId")
            // Chặn id rỗng hoặc null
            if (teamId.isBlank()) {
                Log.w("TeamRepo", "getTeamById skipped: blank id")
                return null
            }

            // Nếu teamId là dạng "teams/abc123" thì cắt lấy "abc123"
            val cleanId = teamId.substringAfterLast('/')

            val doc = collection.document(cleanId).get().await()
            if (!doc.exists()) {
                Log.w("TeamRepo", "Team not found: $cleanId")
                return null
            }

            // Chuyển thành object và fallback id nếu cần
            doc.toObject(Team::class.java)?.let { team ->
                if (team.id.isBlank()) team.copy(id = doc.id) else team
            }
        } catch (e: Exception) {
            Log.e("TeamRepo", "Failed to get team by id: $teamId", e)
            null
        }
    }


    suspend fun getAllTeams(): List<Team> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Team::class.java) }
        } catch (e: Exception) {
            Log.e("TeamRepo", "Failed to get all teams: ", e)
            emptyList()
        }
    }

    suspend fun getTeamsByUserId(userId: String): List<Team> {
        return try {
            val snapshot = collection.whereArrayContains("members", userId).get().await()
            snapshot.documents.mapNotNull { it.toObject(Team::class.java) }
        } catch (e: Exception) {
            Log.e("TeamRepo", "Failed to get teams by userId: ", e)
            emptyList()
        }
    }
} 