package com.example.to_do_app.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.to_do_app.R
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens

@Composable
fun StartPage(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        
        // App Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF4040)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "App Icon",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Title
        Text(
            text = "Let's Get Started!",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 30.sp,
            ),
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle
        Text(
            text = "Let's dive in to your account",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 16.sp,
            ),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(50.dp))
        
        // Social Login Buttons
        SocialLoginButton(
            icon = painterResource(R.drawable.google), // Google icon placeholder
            text = "Continue with Google",
            backgroundColor = Color.White,
            textColor = Color.Black,
            borderColor = Color.LightGray
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SocialLoginButton(
            icon = painterResource(R.drawable.google), // Apple icon placeholder
            text = "Continue with Apple",
            backgroundColor = Color.White,
            textColor = Color.Black,
            borderColor = Color.LightGray
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SocialLoginButton(
            icon = painterResource(R.drawable.facebook), // Facebook icon placeholder
            text = "Continue with Facebook",
            backgroundColor = Color.White,
            textColor = Color.Black,
            borderColor = Color.LightGray
        )


        Spacer(modifier = Modifier.weight(1f))
        
        // Sign Up Button
        Button(
            onClick = {
                navController.navigate(Screens.RegisterPage.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF4040)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Sign up",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 16.sp,
                ),
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sign In Button
        Button(
            onClick = {
                navController.navigate(Screens.LoginPage.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFE5E5)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 16.sp,
                ),
                color = Color(0xFFFF4040)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Footer Links
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 14.sp,
                ),
                color = Color.Gray,
                modifier = Modifier.clickable { /* TODO */ }
            )
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 14.sp,
                ),
                color = Color.Gray,
                modifier = Modifier.clickable { /* TODO */ }
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SocialLoginButton(
    icon: Painter,
    text: String,
    backgroundColor: Color,
    textColor: Color,
    borderColor: Color
) {
    Button(
        onClick = { /* TODO: Handle social login */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, borderColor, RoundedCornerShape(28.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint =  Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 16.sp,
                ),
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartPagePreview() {
    To_do_appTheme {
//        StartPage()
    }
}
