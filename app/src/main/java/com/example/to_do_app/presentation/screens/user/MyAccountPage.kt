package com.example.to_do_app.presentation.screens.user

import BottomNavigation
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.to_do_app.R
import com.example.to_do_app.components.CategoryTopAppBar
import com.example.to_do_app.domain.User
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens

@Composable
fun MyAccountPage(
    navController: NavController,
    userVm: AuthViewModel = viewModel()
) {
    val currentUser by userVm.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        userVm.fetchAndSetCurrentUser()
    }

    Scaffold(
        topBar = {
            CategoryTopAppBar(
                text = "Account",
                onBackClick = {
                    navController.popBackStack()
                },
                iconPainter = painterResource(R.drawable.setting),
                onClick = {
                    // TODO: Handle settings click
                }
            )
        },
        bottomBar = {
            BottomNavigation(navController)
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
//                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Card
                AccountProfileCard(currentUser, onClick = {
                    navController.navigate(Screens.PersonalInfoPage.route)
                })

                Spacer(modifier = Modifier.height(24.dp))

                // Settings List
                SettingsSection()

                Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for navigation
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTopBar() {
    TopAppBar(
        title = {
            CategoryTopAppBar(
                text = "Notification",
                onBackClick = {
                    // TODO: Handle back navigation
                },
                iconPainter = painterResource(R.drawable.setting),
                onClick = {
                    // TODO: Handle settings click
                }
            )
        },
        actions = {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color(0xFF5B5EF4)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun AccountProfileCard(
    user: User? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .padding(start = 16.dp, end = 16.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5B5EF4))
                    .border(2.dp, Color.White, CircleShape)
            ) {
                if (user?.imgUrl != null) {
                    AsyncImage(
                        model = user.imgUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(60.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            // Profile Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = user?.name ?: "Name default",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = user?.email ?: "Email defaults",
                    color = Color.Gray,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_regular))
                    ),
                )
            }

            // Arrow Icon
            Icon(
                painter = painterResource(R.drawable.rightarrow),
                contentDescription = "Details",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SettingsSection(
    navController: NavController = rememberNavController()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingsItem(icon = Icons.Default.Settings, title = "Preferences")
            SettingsItem(icon = Icons.Default.DateRange, title = "Calendar & Time", onClick = {
                navController.navigate(Screens.CalenderTimePage.route)
            })
            SettingsItem(icon = Icons.Default.Notifications, title = "Reminder")
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notification",
                onClick = { navController.navigate(Screens.NotificationsPage.route) })
            SettingsItem(icon = Icons.Default.Settings, title = "Account & Security", onClick = {
                navController.navigate(Screens.SecurityPage.route)
            })
            SettingsItem(icon = Icons.Default.Create, title = "Payment Methods")
            SettingsItem(icon = Icons.Default.Refresh, title = "Billing & Subscriptions")
            SettingsItem(icon = Icons.Default.List, title = "Linked Accounts")
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        when {
            icon != null -> {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.DarkGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            iconPainter != null -> {
                Icon(
                    painter = iconPainter,
                    contentDescription = title,
                    tint = Color.DarkGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        // Title
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
            color = Color.Black
        )

        // Arrow Icon
        Icon(
            painter = painterResource(R.drawable.rightarrow),
            contentDescription = "Details",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyAccountPagePreview() {
    To_do_appTheme {
//        MyAccountPage()
    }
}
