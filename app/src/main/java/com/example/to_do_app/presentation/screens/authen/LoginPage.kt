package com.example.to_do_app.presentation.screens.authen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.to_do_app.R
import com.example.to_do_app.components.ButtonSignUp
import com.example.to_do_app.components.CustomInputField
import com.example.to_do_app.components.RoundedCheckbox
import com.example.to_do_app.components.SignUpTopBar
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens

@Composable
fun LoginPage(
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }


    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    var loginError by remember { mutableStateOf<String?>(null) }
    val loginState by authViewModel.loginState.collectAsState() // Giả sử ViewModel có loginState

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Scaffold(
        topBar = {
            SignUpTopBar(text = "Welcome Back! 👋", onBackClick = {
                navController.popBackStack()
            })
        },
        bottomBar = {
            ButtonSignUp("Sign In", onClick = {
                if (isValidEmail(email.trim())) {
                    authViewModel.login(email = email, password = password)
                } else {
                    loginError = "Invalid email format"
                }
            }, modifier = Modifier)


            if (loginError != null) {
                Text(
                    text = loginError!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            // Theo dõi trạng thái đăng nhập
            LaunchedEffect(loginState) {
                when (loginState) {
                    is AuthViewModel.LoginState.Success -> {
                        loginError = null
                        navController.navigate(Screens.HomePage.route) // Đổi thành page bạn muốn
                    }
                    is AuthViewModel.LoginState.Error -> {
                        loginError = (loginState as AuthViewModel.LoginState.Error).message
                    }
                    else -> {}
                }
            }

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Sign in to continue your journey of deep work, clear goals, and mindful productivity.",
                color = Color.Gray,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_regular))
                ),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
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

            Spacer(modifier = Modifier.height(24.dp))

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

            CustomInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Remember me and Forgot Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundedCheckbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        checkedColor = Color(0xFFFF4040),
                        uncheckedColor = Color.LightGray,
                        cornerRadius = 4
                    )
                    Text(
                        text = "Remember me",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        )
                    )
                }
                
                Text(
                    text = "Forgot Password?",
                    color = Color(0xFFFF4040),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate(Screens.ForgotPassWordPage.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Don't have an account
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                )
                Text(
                    text = "Sign up",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    color = Color(0xFFFF4040),
                    modifier = Modifier.clickable {
                        navController.navigate(Screens.RegisterPage.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Or continue with
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                    contentDescription = "Apple Login"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign In Button

        }
    }
}


@Composable
@Preview
fun ForgotPasswordPreview() {
    To_do_appTheme {
        LoginPage()
    }
}
