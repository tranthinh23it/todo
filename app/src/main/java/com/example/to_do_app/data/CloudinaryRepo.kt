package com.example.to_do_app.data

import android.content.Context
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class CloudinaryRepo {
    private val firestore = Firebase.firestore
    private val reviewsCollection = firestore.collection("reviews")

    suspend fun uploadImageToCloudinary(context: Context, imageFile: File): String? {
        return withContext(Dispatchers.IO) {
            try {
                val config: MutableMap<String, String> = mutableMapOf()
                config["cloud_name"] = "deszzpgc1"
                config["api_key"] = "286253363248734"
                config["api_secret"] = "la0MlXREjPyaw0o7E1eL4Q8i0Eo"

                val cloudinary = Cloudinary(config)
                val uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap())

                uploadResult["secure_url"] as? String
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun addImageUrlToReview(reviewId: String, imageUrl: String) {
        reviewsCollection.document(reviewId).update(
            "imageUrls", FieldValue.arrayUnion(imageUrl)
        ).await()
    }

    suspend fun getReviewImageUrls(reviewId: String): List<String> {
        return try {
            val reviewDoc = firestore.collection("reviews").document(reviewId).get().await()
            val imageUrls = reviewDoc.get("imageUrls") as? List<String> ?: emptyList()
            imageUrls
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}