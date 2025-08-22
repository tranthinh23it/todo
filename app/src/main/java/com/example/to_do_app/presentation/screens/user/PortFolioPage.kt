//package com.example.to_do_app.presentation.screens
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.to_do_app.R
//import com.example.to_do_app.domain.Project
//import com.example.to_do_app.ui.theme.To_do_appTheme
//
//@Composable
//fun PortfolioPage() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF8F9FA))
//    ) {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            item {
//                PortfolioTopBar()
//                Spacer(modifier = Modifier.height(24.dp))
//
//                ProfileCard()
//                Spacer(modifier = Modifier.height(24.dp))
//
//                StatsCard()
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Text(
//                    text = "My Projects",
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(start = 4.dp, bottom = 16.dp)
//                )
//            }
//
//            items(projectsList) { project ->
//                ProjectCard(project)
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            item {
//                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
//            }
//        }
//
//        FloatingActionButton(
//            onClick = { /* TODO */ },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(24.dp),
//            containerColor = MaterialTheme.colorScheme.primary
//        ) {
//            Icon(
//                imageVector = Icons.Default.Add,
//                contentDescription = "Add Project",
//                tint = Color.White
//            )
//        }
//    }
//}
//
//@Composable
//fun PortfolioTopBar() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        IconButton(onClick = { /* TODO: Navigate back */ }) {
//            Icon(
//                imageVector = Icons.Default.ArrowBackIosNew,
//                contentDescription = "Back"
//            )
//        }
//        Text(
//            text = "Portfolio",
//            style = MaterialTheme.typography.titleLarge,
//            fontWeight = FontWeight.Bold
//        )
//        IconButton(onClick = { /* TODO */ }) {
//            Icon(
//                imageVector = Icons.Default.MoreVert,
//                contentDescription = "More Options"
//            )
//        }
//    }
//}
//
//@Composable
//fun ProfileCard() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(
//            modifier = Modifier.padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual profile image
//                contentDescription = "Profile Image",
//                modifier = Modifier
//                    .size(120.dp)
//                    .clip(CircleShape)
//                    .background(MaterialTheme.colorScheme.primaryContainer)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "John Doe",
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold
//            )
//
//            Text(
//                text = "Senior Software Developer",
//                style = MaterialTheme.typography.bodyLarge,
//                color = Color.Gray
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "Passionate developer with 5+ years of experience in mobile app development. Specialized in Android and Kotlin.",
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.DarkGray
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                ContactButton(Icons.Default.Email, "Email")
//                ContactButton(Icons.Default.Phone, "Call")
//                ContactButton(Icons.Default.Share, "Share")
//            }
//        }
//    }
//}
//
//@Composable
//fun ContactButton(icon: ImageVector, text: String) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        OutlinedButton(
//            onClick = { /* TODO */ },
//            shape = CircleShape,
//            modifier = Modifier.size(56.dp),
//            contentPadding = PaddingValues(0.dp)
//        ) {
//            Icon(
//                imageVector = icon,
//                contentDescription = text,
//                tint = MaterialTheme.colorScheme.primary
//            )
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = text,
//            style = MaterialTheme.typography.labelSmall,
//            color = Color.Gray
//        )
//    }
//}
//
//@Composable
//fun StatsCard() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            StatItem("Projects", "24")
//            VerticalDivider()
//            StatItem("Completed", "18")
//            VerticalDivider()
//            StatItem("Reviews", "4.8")
//        }
//    }
//}
//
//@Composable
//fun VerticalDivider() {
//    Box(
//        modifier = Modifier
//            .width(1.dp)
//            .height(50.dp)
//            .background(Color.LightGray.copy(alpha = 0.5f))
//    )
//}
//
//@Composable
//fun StatItem(label: String, value: String) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = value,
//            style = MaterialTheme.typography.headlineMedium,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.primary
//        )
//        Text(
//            text = label,
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color.Gray
//        )
//    }
//}
//
//@Composable
//fun ProjectCard(project: Project) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(48.dp)
//                        .clip(RoundedCornerShape(12.dp))
//                        .background(project.iconBackground),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = project.icon,
//                        contentDescription = null,
//                        tint = Color.White,
//                        modifier = Modifier.size(28.dp)
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                Column {
//                    Text(
//                        text = project.name,
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = project.type,
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.Gray
//                    )
//                }
//
//                Spacer(modifier = Modifier.weight(1f))
//
//                Box(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(16.dp))
//                        .background(
//                            if (project.isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
//                        )
//                        .padding(horizontal = 12.dp, vertical = 6.dp)
//                ) {
//                    Text(
//                        text = if (project.isCompleted) "Completed" else "In Progress",
//                        style = MaterialTheme.typography.labelSmall,
//                        color = if (project.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = project.description,
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.DarkGray
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            if (!project.isCompleted) {
//                LinearProgressIndicator(
//                    progress = project.progress,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(8.dp)
//                        .clip(RoundedCornerShape(4.dp)),
//                    color = MaterialTheme.colorScheme.primary,
//                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(
//                        text = "Progress",
//                        style = MaterialTheme.typography.labelSmall,
//                        color = Color.Gray
//                    )
//                    Text(
//                        text = "${(project.progress * 100).toInt()}%",
//                        style = MaterialTheme.typography.labelSmall,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                ProjectDetail(Icons.Default.DateRange, project.date)
//                ProjectDetail(Icons.Default.Group, "${project.teamSize} members")
//                ProjectDetail(Icons.Default.StarBorder, project.rating.toString())
//            }
//        }
//    }
//}
//
//@Composable
//fun ProjectDetail(icon: ImageVector, text: String) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = null,
//            tint = Color.Gray,
//            modifier = Modifier.size(16.dp)
//        )
//        Spacer(modifier = Modifier.width(8.dp))
//        Text(
//            text = text,
//            style = MaterialTheme.typography.labelMedium,
//            color = Color.Gray
//        )
//    }
//}
//
////// Data class for Project
////data class Project(
////    val name: String,
////    val type: String,
////    val description: String,
////    val icon: ImageVector,
////    val iconBackground: Color,
////    val isCompleted: Boolean,
////    val progress: Float = 0f,
////    val date: String,
////    val teamSize: Int,
////    val rating: Double
////)
//
//// Sample projects data
//val projectsList = listOf(
//    Project(
//        name = "Task Manager App",
//        type = "Mobile App",
//        description = "A comprehensive task management application with reminders, categories, and progress tracking.",
//        icon = Icons.Default.CheckCircle,
//        iconBackground = Color(0xFF6200EE),
//        isCompleted = true,
//        date = "Jan 2023",
//        teamSize = 4,
//        rating = 4.8
//    ),
//    Project(
//        name = "E-commerce Platform",
//        type = "Web Development",
//        description = "Online shopping platform with product catalog, cart functionality, and secure payment integration.",
//        icon = Icons.Default.ShoppingCart,
//        iconBackground = Color(0xFF03DAC5),
//        isCompleted = false,
//        progress = 0.75f,
//        date = "Mar 2023",
//        teamSize = 6,
//        rating = 4.5
//    ),
//    Project(
//        name = "Fitness Tracker",
//        type = "Mobile App",
//        description = "Health and fitness tracking app with workout plans, nutrition logging, and progress analytics.",
//        icon = Icons.Default.Favorite,
//        iconBackground = Color(0xFFB00020),
//        isCompleted = false,
//        progress = 0.35f,
//        date = "May 2023",
//        teamSize = 3,
//        rating = 4.2
//    )
//)
//
//@Preview(showBackground = true)
//@Composable
//fun PortfolioPagePreview() {
//    To_do_appTheme {
//        PortfolioPage()
//    }
//}
