package com.example.to_do_app.presentation.screens.authen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_app.R
import com.example.to_do_app.components.ButtonSignUp
import com.example.to_do_app.components.CustomInputField
import com.example.to_do_app.components.SignUpTopBar
import com.example.to_do_app.ui.theme.To_do_appTheme

@Composable
fun ForgotPasswordPage() {
    var email by remember { mutableStateOf("example-email@gmail.com") }

    Scaffold(
        topBar = {
            SignUpTopBar(text = "Forgot Password?", onBackClick = {})
        },
        bottomBar = {
            ButtonSignUp("Send Code", onClick = { /*TODO*/ }, modifier = Modifier)
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
                text = "Enter the email you used to sign up with Focuso. We'll send you a one-time code to reset your password.",
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
                text = "Registered email address",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomInputField(
                value = email,
                onValueChange = { email = it },
                label = "andrew.ainsley@yourdomain.com",
                leadingIcon = painterResource(R.drawable.mail),

            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Composable
@Preview
fun ForgotPasswordsPreview() {
    To_do_appTheme {
        ForgotPasswordPage()
    }
}
