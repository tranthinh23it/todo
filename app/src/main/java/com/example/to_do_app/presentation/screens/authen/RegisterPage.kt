package com.example.to_do_app.presentation.screens.authen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_app.R
import com.example.to_do_app.components.ButtonSignUp
import com.example.to_do_app.components.CustomInputField
import com.example.to_do_app.components.PasswordTextField
import com.example.to_do_app.components.RoundedCheckbox
import com.example.to_do_app.components.SignUpTopBar
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens

@Composable
fun RegisterPage(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SignUpTopBar(text = "Join Focuso Today! ⏳", onBackClick = {})
        },
        bottomBar = {
            ButtonSignUp("Sign Up", onClick = {
                val trimmedEmail = email.trim()
                val trimmedPassword = password.trim()
                authViewModel.signUp(trimmedEmail, trimmedPassword,"")
                navController.navigate(Screens.LoginPage.route)
            }, modifier = Modifier)
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
//                .padding(horizontal = 24.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Join thousands who are turning distractions into deep work.",
                color = Color.Gray,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                ),
                modifier = Modifier.padding(
                    start = 16.dp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
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
                onValueChange = {
                    email = it
                    emailError = if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()) null else "Invalid Email"
                },
                label = "Email",
                leadingIcon = painterResource(R.drawable.mail)
            )

            if (emailError != null) {
                Text(
                    text = emailError!!,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Password Field
            Text(
                text = "Password",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Terms and Conditions Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 24.dp)
            ) {
                RoundedCheckbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    checkedColor = Color(0xFFFF4040),
                    uncheckedColor = Color.LightGray,
                    cornerRadius = 12
                )

                val annotatedString = buildAnnotatedString {
                    append("I agree to Focuso ")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFFFF4040),
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("Terms & Conditions")
                    }
                    append(".")
                }

                Text(
                    text = annotatedString,
                    modifier = Modifier
                        .clickable { termsAccepted = !termsAccepted }
                        .padding(start = 8.dp),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Already have an account
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
                Text(
                    text = "Sign in",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    color = Color(0xFFFF4040),
                    modifier = Modifier.clickable {
                        navController.navigate(Screens.LoginPage.route)
                    }

                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Or continue with
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
                Text(
                    text = "or continue with",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_regular))
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Login Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialConnected(
                    logoIcon = R.drawable.google,
                    onClick = { /* TODO: Handle Google login */ },
                    contentDescription = "Google Login"
                )
                SocialConnected(
                    logoIcon = R.drawable.facebook,
                    onClick = { /* TODO: Handle Facebook login */ },
                    contentDescription = "Facebook Login"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign Up Button
        }
    }
}

@Composable
fun SocialIcon(icon: Any) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, Color.LightGray, CircleShape)
            .clip(CircleShape)
            .clickable { /* TODO: Handle social login */ },
        contentAlignment = Alignment.Center
    ) {
        when (icon) {
            is ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            is Int -> {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SocialConnected(
    @DrawableRes logoIcon : Int,
    onClick: ()-> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .size(92.dp,64.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .background(Color(0xFFFFFFFF),
                shape = RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable (onClick = onClick ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(logoIcon),
            contentDescription = contentDescription,
            modifier = Modifier.size(25.5.dp,24.dp)
        )
    }
}

@Preview
@Composable
fun LoginPreview() {
    To_do_appTheme {
//        RegisterPage()
    }
}
