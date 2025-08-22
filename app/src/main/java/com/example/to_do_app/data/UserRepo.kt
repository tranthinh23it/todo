package com.example.to_do_app.data



import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.to_do_app.domain.User
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.InputStream

class UserRepo {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = Firebase.storage

    private var currentContext: Context? = null

    // Kept the explicit setContext function
    fun setContext(context: Context) {
        this.currentContext = context
    }

    suspend fun addUser(user: User): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(user.userId)
                .set(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepo", "Failed to add user: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, userName: String): Pair<Boolean, String?> {
        // Kiểm tra định dạng email
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        if (!email.matches(emailPattern.toRegex())) {
            return Pair(false, "Invalid email format")
        }

        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                val user = User(
                    userId = userId, email = email, name = userName, password = password
                )
                firestore.collection("users").document(userId).set(user).await()
                Log.d("UserRepository", "Sign up successful and user data stored for $email")
                Pair(true, "Sign up successful!")
            } else {
                Pair(false, "Sign up failed: No user ID")
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("UserRepository", "Invalid credentials: ${e.localizedMessage}")
            Pair(false, "Invalid credentials: ${e.localizedMessage}")
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("UserRepository", "User already exists: ${e.localizedMessage}")
            Pair(false, "Email already in use")
        } catch (e: Exception) {
            Log.e("UserRepository", "Sign up failed: ${e.localizedMessage}", e)
            Pair(false, e.localizedMessage ?: "Sign up failed")
        }
    }


    suspend fun login(email: String, password: String): Pair<Boolean, String?> {
        if (email.isBlank() || password.isBlank()) {
            return Pair(false, "Email or password cannot be empty")
        }
        return try {
            Log.d("UserRepository", "Attempting login for $email")
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Log.d("UserRepository", "Login successful for $email")
            Pair(true, "Login successful!")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed: ${e.localizedMessage}", e)
            Pair(false, e.localizedMessage ?: "Login failed")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
    // Hàm lấy User theo ID
    suspend fun getUserById(userId: String): User? {
        return try {
            Log.d("fetch", "userId: $userId")
            val document = firestore
                .collection("users")
                .document(userId.trim())
                .get()
                .await()

            if (document.exists()) {
//                Log.d("fetch", "Document data: ${document.data}")
                return document.toObject(User::class.java)
            } else {
                Log.d("fetch", "Document does not exist")
                return null
            }

        } catch (e: Exception) {
            Log.e("Repository", "Error fetching user: ${e.message}")
            null
        }
    }


    suspend fun uploadAvatarAndUpdateProfile(userId: String, imageUri: Uri): Result<String> {
        return try {
            Log.d("UserRepository", "Converting image to Base64 for user: $userId with image URI: $imageUri")
            val base64Image = uriToBase64(imageUri)
            if (base64Image == null) {
                Result.failure(Exception("Failed to convert image to Base64"))
            } else {
                Log.d("UserRepository", "Saving Base64 image to Firestore for user: $userId")
                firestore.collection("users")
                    .document(userId)
                    .update("profileImageBase64", base64Image)
                    .await() // Use await for coroutine-friendly operation
                Log.d("UserRepository", "Successfully saved Base64 image to Firestore for user: $userId")
                Result.success(base64Image) // You might want to return a success message or the Base64 string
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to update profile image (Base64) for user: $userId", e)
            Result.failure(e)
        }
    }

    private fun uriToBase64(imageUri: Uri): String? {
        return try {
            // Ensure context is available
            val currentContext =currentContext
            if (currentContext == null) {
                Log.e("UserRepository", "Context is null, cannot convert URI to Base64")
                return null
            }
            val inputStream: InputStream? = currentContext.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream) // Adjust format and quality
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserProfileImageBase64(userId: String): Result<String?> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            val base64Image = document.getString("profileImageBase64")
            Result.success(base64Image)
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to retrieve Base64 image for user: $userId", e)
            Result.failure(e)
        }
    }

    fun updateUser(user: User): Task<Void> {
        Log.d("updateUser","User: $user")
        return firestore
            .collection("users")
            .document(user.userId)
            .set(user)
    }

    suspend fun fetchCurrentUserData(): User? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            return try {
                val document = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                if (document.exists()) {
                    val userData = document.toObject(User::class.java)
                    Log.d("LoginViewModel", "Fetched User: $userData")
                    userData
                } else {
                    Log.d("LoginViewModel", "No user document found for UID: ${currentUser.uid}")
                    null
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Failed to fetch user data: ${e.message}")
                null
            }
        } else {
            Log.e("LoginViewModel", "No current user after login")
            return null
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            Log.e("AuthRepo", "User not logged in")
            return
        }

        val email = user.email
        if (email == null) {
            Log.e("AuthRepo", "No email associated with user")
            return
        }

        try {
            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            user.reauthenticate(credential).await()

            user.updatePassword(newPassword).await()

            // Optionally, update any relevant fields in Firestore if needed
            firestore.collection("users").document(user.uid)
                .update("lastPasswordChange", System.currentTimeMillis()) // Example field
                .await()

            Log.d("AuthRepo", "Password changed successfully")
        } catch (e: Exception) {
            Log.e("AuthRepo", "Failed to change password: ${e.message}")
            throw e // Re-throw the exception to be handled by the caller
        }
    }

    suspend fun deleteUserAccount(password: String): Result<String> {
        val user = firebaseAuth.currentUser

        if (user == null) {
            return Result.failure(Exception("No user is currently signed in."))
        }

        val email = user.email
        if (email.isNullOrEmpty()) {
            return Result.failure(Exception("No email associated with user."))
        }

        return try {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()

            // Delete the user document from Firestore
            firestore.collection("users").document(user.uid)
                .delete()
                .await()

            // Finally, delete the Firebase Auth user
            user.delete().await()

            Result.success("User account deleted successfully.")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Delete account failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<String> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.d("UserRepo", "Password reset email sent to $email")
            Result.success("Password reset email sent to $email")
        } catch (e: Exception) {
            Log.e("UserRepo", "Failed to send password reset email: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Modified function to use the set context
    private fun uriToBase64WithContext(): String? {
        return try {
            val currentContext = currentContext
            if (currentContext == null) {
                Log.e("UserRepository", "Context is null, cannot convert URI to Base64")
                return null
            }
            val inputStream: InputStream? = currentContext.contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Modified upload function to use the context-aware Base64 conversion
    suspend fun uploadBase64ImageAndUpdateProfile(userId: String, imageUri: Uri): Result<String> {
        this.imageUri = imageUri // Store the imageUri temporarily
        return try {
            Log.d("UserRepository", "Converting image to Base64 for user: $userId with image URI: $imageUri")
            val base64Image = uriToBase64WithContext()
            if (base64Image == null) {
                Result.failure(Exception("Failed to convert image to Base64"))
            } else {
                Log.d("UserRepository", "Saving Base64 image to Firestore for user: $userId")
                firestore.collection("users")
                    .document(userId)
                    .update("profileImageBase64", base64Image)
                    .await()
                Log.d("UserRepository", "Successfully saved Base64 image to Firestore for user: $userId")
                Result.success(base64Image)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to update profile image (Base64) for user: $userId", e)
            Result.failure(e)
        } finally {
            this.imageUri = null // Clear the temporary imageUri
        }
    }
    private var imageUri: Uri? = null // Temporary storage for the Uri

    suspend fun deleteUserById(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .delete()
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = firestore.collection("users")
                .get()
                .await()

            val userList = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
            Log.d("UserRepository", "Fetched ${userList.size} users from Firestore")
            userList
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to fetch all users: ${e.message}", e)
            emptyList()
        }
    }

    fun updateFcmToken(userId: String, token: String) {
       firestore.collection("users").document(userId)
            .update("fcmToken", token)
    }

    suspend fun getUserByTeamId(teamId: String): List<User> {
        return try {
            Log.d("UserRepo", "Fetching users for team: $teamId")
            val snapshot = firestore.collection("users")
                .whereEqualTo("team", teamId)
                .get()
                .await()

            val userList = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
            Log.d("UserRepo", "Fetched ${userList.size} users for team: $teamId")
            userList
        } catch (e: Exception) {
            Log.e("UserRepo", "Failed to fetch users by team ID: ${e.message}", e)
            emptyList()
        }
    }

}
