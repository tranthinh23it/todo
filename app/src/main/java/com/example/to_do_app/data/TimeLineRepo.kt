package com.example.to_do_app.data

import android.util.Log
import com.example.to_do_app.domain.TimeLine
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TimeLineRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("timelines")

    suspend fun addTimeLine(timeLine: TimeLine): Result<Unit> {
        return try {
            collection.document(timeLine.id).set(timeLine).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TimeLineRepo", "Failed to add timeline: ", e)
            Result.failure(e)
        }
    }

    suspend fun updateTimeLine(timeLine: TimeLine): Result<Unit> {
        return try {
            collection.document(timeLine.id).set(timeLine).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TimeLineRepo", "Failed to update timeline: ", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTimeLine(timeLineId: String): Result<Unit> {
        return try {
            collection.document(timeLineId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TimeLineRepo", "Failed to delete timeline: ", e)
            Result.failure(e)
        }
    }

    suspend fun getTimeLineById(timeLineId: String): TimeLine? {
        return try {
            val doc = collection.document(timeLineId).get().await()
            doc.toObject(TimeLine::class.java)
        } catch (e: Exception) {
            Log.e("TimeLineRepo", "Failed to get timeline by id: ", e)
            null
        }
    }

    suspend fun getAllTimeLines(): List<TimeLine> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject(TimeLine::class.java) }
        } catch (e: Exception) {
            Log.e("TimeLineRepo", "Failed to get all timelines: ", e)
            emptyList()
        }
    }

    suspend fun getTimeLinesByProjectId(projectId: String): List<TimeLine> {
        return try {
            Log.d("TimeLineRepo", "Getting timelines by projectId: $projectId")
            val snapshot = collection.whereEqualTo("projectId", projectId).get().await()
            snapshot.documents.mapNotNull { it.toObject(TimeLine::class.java) }
        } catch (e: Exception) {
            Log.e("TimeLineRepo", "Failed to get timelines by projectId: ", e)
            emptyList()
        }
    }
}