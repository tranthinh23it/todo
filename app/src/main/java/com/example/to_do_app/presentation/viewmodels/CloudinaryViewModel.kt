package com.example.to_do_app.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_do_app.data.CloudinaryRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


class CloudinaryViewModel(private val repository: CloudinaryRepo = CloudinaryRepo()) : ViewModel() {

    private val _imageUrls = MutableStateFlow<List<String>>(emptyList())
    val imageUrls: StateFlow<List<String>> = _imageUrls.asStateFlow()

    fun fetchReviewImages(reviewId: String) {
        viewModelScope.launch {
            _imageUrls.value = repository.getReviewImageUrls(reviewId)
        }
    }

    suspend fun uploadMultipleImages(context: Context, uris: List<Uri>): List<String> {
        val urls = mutableListOf<String>()
        for (uri in uris) {
            val file = uriToFile(context, uri)
            val url = repository.uploadImageToCloudinary(context, file)
            if (url != null) {
                urls.add(url)
            }
        }
        return urls
    }

    suspend fun uploadImage(context: Context, uri: Uri): String? {
        val file = uriToFile(context, uri)
        return repository.uploadImageToCloudinary(context, file)
    }


    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Cannot open URI")
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        inputStream.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
        return file
    }
}
