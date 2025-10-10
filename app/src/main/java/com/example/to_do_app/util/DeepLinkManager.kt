package com.example.to_do_app.util

import android.net.Uri
import android.util.Log

object DeepLinkManager {
    private var pendingDeepLink: String? = null
    
    fun setPendingDeepLink(deepLink: String) {
        Log.d("DeepLinkManager", "Setting pending deep link: $deepLink")
        pendingDeepLink = deepLink
    }
    
    fun getAndClearPendingDeepLink(): String? {
        return pendingDeepLink.also { 
            pendingDeepLink = null 
            Log.d("DeepLinkManager", "Cleared pending deep link: $it")
        }
    }
    
    fun parseDeepLink(deepLink: String): DeepLinkData? {
        return try {
            val uri = Uri.parse(deepLink)
            if (uri.scheme == "todoapp") {
                when (uri.host) {
                    "task" -> {
                        val taskId = uri.lastPathSegment
                        DeepLinkData(
                            type = DeepLinkType.TASK,
                            id = taskId ?: "",
                            tab = uri.getQueryParameter("tab"),
                            highlight = uri.getQueryParameter("highlight")
                        )
                    }
                    "project" -> {
                        val projectId = uri.lastPathSegment
                        DeepLinkData(
                            type = DeepLinkType.PROJECT,
                            id = projectId ?: "",
                            tab = uri.getQueryParameter("tab")
                        )
                    }
                    "notifications" -> {
                        DeepLinkData(
                            type = DeepLinkType.NOTIFICATIONS,
                            id = ""
                        )
                    }
                    "home" -> {
                        DeepLinkData(
                            type = DeepLinkType.HOME,
                            id = ""
                        )
                    }
                    else -> null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DeepLinkManager", "Error parsing deep link: $deepLink", e)
            null
        }
    }
}

enum class DeepLinkType {
    TASK,
    PROJECT,
    NOTIFICATIONS,
    HOME
}

data class DeepLinkData(
    val type: DeepLinkType,
    val id: String,
    val tab: String? = null,
    val highlight: String? = null
)
