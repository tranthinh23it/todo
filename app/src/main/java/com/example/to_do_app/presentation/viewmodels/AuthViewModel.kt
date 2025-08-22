package com.example.to_do_app.presentation.viewmodels


import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.to_do_app.data.UserRepo
import com.example.to_do_app.domain.User
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class  AuthViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private val authRepository = UserRepo()


    private val _authResult = MutableLiveData<Pair<Boolean, String?>>()
    val authResult: LiveData<Pair<Boolean, String?>> = _authResult

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _uploadState = MutableStateFlow<Result<String>?>(null)
    val uploadState: StateFlow<Result<String>?> = _uploadState

    private val _changePasswordResult = MutableStateFlow<Result<String>?>(null)
    val changePasswordResult: StateFlow<Result<String>?> = _changePasswordResult

    private val _deleteAccountStatus = MutableStateFlow<Result<Boolean>>(Result.success(false))
    val deleteAccountStatus: StateFlow<Result<Boolean>> = _deleteAccountStatus

    private val _profileImageBase64 = mutableStateOf<String?>(null)
    val profileImageBase64: State<String?> = _profileImageBase64

    private val _resetPasswordStatus = MutableLiveData<Result<String>?>(null)
    val resetPasswordStatus: LiveData<Result<String>?> = _resetPasswordStatus

    // LoginState sealed class
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: User?) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private fun uriToBase64(imageUri: Uri): String? {
        return try {
            val inputStream: InputStream? = getApplication<Application>().contentResolver.openInputStream(imageUri)
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

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun signUp(
        email: String,
        password: String,
        userName: String,
    ) {
        viewModelScope.launch {
            val result = authRepository.signUp(
                email, password, userName
            )
            _authResult.postValue(result)
        }
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        Log.d("AuthViewModel", "Attempting login with email: $email")

        // Kiểm tra nếu là admin
        if (email.equals("admin", ignoreCase = true) && password == "123") {
            _authResult.postValue(Pair(true, "Admin login successful"))
            return
        }

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            Log.d(
                "AuthViewModel",
                "Login result: Success=${result.first}, Message=${result.second}"
            )
            _authResult.postValue(result)
            if (result.first) {
                val user = authRepository.fetchCurrentUserData()
                _loginState.value = LoginState.Success(user)
            } else {
                _loginState.value = LoginState.Error(result.second ?: "Login failed")
            }
        }
    }

    fun getCurrentUser(): User? {
        return _currentUser.value
    }

    fun fetchAndSetCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.fetchCurrentUserData()
            _currentUser.value = user
            Log.d("AuthViewModel", "Current user set: ${user?.userId}")
        }
    }
    // MutableStateFlow cho user
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Trong AuthViewModel:
    suspend fun getUserById(userId: String): User? {
        return authRepository.getUserById(userId)
    }
    // Hàm lấy User theo ID
//    fun getUserById(userId: String) : User?{
//        viewModelScope.launch {
//            val userData = authRepository.getUserById(userId)
//            _user.value = userData
//        }
//        return _user.value
//    }
//    suspend fun getUserById(userId: String): User? {
//        return try {
//            authRepository.getUserById(userId)
//        } catch (e: Exception) {
//            Log.e("ViewModel", "Error fetching user: ${e.message}")
//            null
//        }
//    }

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authRepository.updateUser(user)
                .addOnSuccessListener {
                    Log.d("AuthViewModel", "User updated successfully.")
                    onResult(true)
                    _currentUser.postValue(user)
                }
                .addOnFailureListener { e ->
                    Log.e("AuthViewModel", "Failed to update user: ${e.message}")
                    onResult(false)
                }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun changePassword(oldPassword: String, newPassword: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.changePassword(oldPassword, newPassword)
                onResult(true)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Change password failed: ${e.message}")
                onResult(false)
            }
        }
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            try {
                authRepository.deleteUserAccount(password)
                _deleteAccountStatus.value = Result.success(true)
            } catch (e: Exception) {
                _deleteAccountStatus.value = Result.failure(e)
            }
        }
    }

    fun updateProfileAvatar(userId: String, imageUri: Uri) {
        viewModelScope.launch {
            _uploadState.value = authRepository.uploadBase64ImageAndUpdateProfile(userId, imageUri)
        }
    }

    fun fetchProfileImageBase64(userId: String) {
        viewModelScope.launch {
            val result = authRepository.getUserProfileImageBase64(userId)
            _profileImageBase64.value = result.getOrNull()
        }
    }

    fun base64ToBitmap(base64String: String?): Bitmap? {
        return base64String?.let {
            Base64.decode(it, Base64.DEFAULT).let { byteArray ->
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPasswordStatus.value = authRepository.resetPassword(email)
        }
    }

    fun clearResetPasswordStatus() {
        _resetPasswordStatus.value = null
    }

    fun updateFcmToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (token != null) {
                authRepository.updateFcmToken(userId, token)
            }
        }
    }

    // MutableStateFlow cho danh sách users theo team
    private val _usersByTeam = MutableStateFlow<List<User>>(emptyList())
    val usersByTeam: StateFlow<List<User>> = _usersByTeam

    fun getUserByTeamId(teamId: String) {
        viewModelScope.launch {
            try {
                val users = authRepository.getUserByTeamId(teamId)
                _usersByTeam.value = users
                Log.d("AuthViewModel", "Fetched ${users.size} users for team: $teamId")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to fetch users by team ID: ${e.message}", e)
                _usersByTeam.value = emptyList()
            }
        }
    }

}
