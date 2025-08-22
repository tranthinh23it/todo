package com.example.to_do_app.presentation.screens.user

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.to_do_app.R
import com.example.to_do_app.components.CategoryTopAppBar
import com.example.to_do_app.components.CustomDatePickerField
import com.example.to_do_app.components.CustomDropdownField
import com.example.to_do_app.components.CustomInputField
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.CloudinaryViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import kotlinx.coroutines.launch

@Composable
fun PersonalInfoPage(
    navController: NavController,
    authVM: AuthViewModel = viewModel(),
    cloudVM : CloudinaryViewModel = viewModel(),
) {
    val currentUser by authVM.currentUser.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        authVM.fetchAndSetCurrentUser()
    }

    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    var dob by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            selectedImage = null
            email = it.email
            name = it.name
            phone = it.phoneNumber
            gender = it.gender
            dob = it.dob
            country = it.address
        }
    }

    Scaffold(
        topBar = {
            CategoryTopAppBar(
                text = "Personal Info",
                onBackClick = {
                    navController.popBackStack()
                },
                iconPainter = painterResource(R.drawable.save),
                onClick = {
                    coroutineScope.launch {
                        currentUser?.let { current ->
                            val uploadedUrl = selectedImage?.let {
                                cloudVM.uploadImage(context, it)
                            } ?: current.imgUrl

                            val updatedUser = current.copy(
                                name = name,
                                dob = dob,
                                address = country,
                                phoneNumber = phone,
                                email = email,
                                imgUrl = uploadedUrl
                            )

                            authVM.updateUser(updatedUser) { success ->
                                if (success) {
                                    Log.d("Update", "User info updated successfully.")
//                                    onSubmit()
//                                    onDismiss()
                                } else {
                                    Log.e("Update", "Failed to update user info.")
                                }
                            }
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
//                        .padding(24.dp),
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image Section
                    Box(
                        modifier = Modifier
                            .padding(top = 24.dp)
//                            .size(117.dp),
                        ,
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        AsyncImage(
                            model = currentUser?.imgUrl, // Replace with actual image URL
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.pencil)
                        )
                        AvatarPickerIcon { uri ->
                            selectedImage = uri
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))


                Text(
                    text = "Full Name",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Email",
                    leadingIcon = painterResource(R.drawable.mail)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                CustomInputField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    leadingIcon = painterResource(R.drawable.mail)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Phone Number",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomInputField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "+1 (646) 555-4099",
                    leadingIcon = painterResource(R.drawable.phone)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Gender",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomDropdownField(
                    options = listOf("Male", "Female"),
                    selectedOption = gender,
                    onOptionSelected = { gender = it },
                    label = "Gender",
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Date of birth",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomDatePickerField(
                    selectedDate = dob,
                    onDateSelected = { dob = it },
                    label = "Date of birth  ",
//                    leadingIcon = painterResource(id = R.drawable.cake) // nếu có icon
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Country",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                CustomDropdownField(
                    options = listOf("Male", "Female"),
                    selectedOption = country,
                    onOptionSelected = { country = it },
                    label = "VietNam",
                )
            }
        }
    }
}

@Composable
fun PersonalInfoField(
    label: String,
    value: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isDropdown: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Handle field click */ },
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.invoke()

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                if (isDropdown) {
                    Icon(
                        painter = painterResource(R.drawable.down),
                        contentDescription = "Dropdown",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Alternative implementation using OutlinedTextField if you prefer input fields
@Composable
fun PersonalInfoFieldAlternative(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    isDropdown: Boolean = false,
    readOnly: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            leadingIcon = leadingIcon,
            trailingIcon = if (isDropdown) {
                {
                    Icon(
                        painter = painterResource(R.drawable.down),
                        contentDescription = "Dropdown",
                        tint = Color.Gray
                    )
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
@Preview
fun PersonalPreview() {
    To_do_appTheme {
//        PersonalInfoPage()

    }
}


@Composable
fun AvatarPickerIcon(
    onImagePicked: (Uri?) -> Unit
) {
    val context = LocalContext.current

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePicked(uri) // Pass the URI back to parent
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*") // Launch image picker if permission granted
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // The Icon which opens the image picker when clicked
    Icon(
        painter = painterResource(R.drawable.pencil),
        contentDescription = "Camera Icon",
        tint = Color.Red,
        modifier = Modifier
            .size(30.dp)
            .background(Color.White, shape = RoundedCornerShape(50))
            .padding(5.dp)
            .clickable {
                // Yêu cầu quyền phù hợp tùy vào API Level
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES // For Android 13+
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE // For older versions
                }
                permissionLauncher.launch(permission) // Request permission before launching picker
            }
    )
}
