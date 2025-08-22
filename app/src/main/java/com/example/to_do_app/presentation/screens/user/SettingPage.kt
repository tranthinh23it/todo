package com.example.to_do_app.presentation.screens.user


import BottomNavigation
import android.net.wifi.SoftApConfiguration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.to_do_app.R
import com.example.to_do_app.components.CategoryTopAppBar
import com.example.to_do_app.presentation.screens.notification.sampleTask
import com.example.to_do_app.presentation.screens.notification.sampleTaskActivity
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens
import kotlin.math.roundToInt


@Composable
fun SettingPage(navController: NavController) {

    var selected by remember { mutableStateOf("FAQ") }
    var fabOffset by remember { mutableStateOf(Offset.Zero) }


    Scaffold(
//        containerColor = Color(0xFFF1FFF3),
        topBar = {
            CategoryTopAppBar(
                text = "Account & Security",
                onBackClick = {
                    navController.popBackStack()
                },
                iconPainter = painterResource(R.drawable.setting),
                onClick = {

                }
            )
        },
        bottomBar = {
            BottomNavigation(navController = navController)
        },
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = Color(0xFF5B5EF4),
                modifier = Modifier
                    .offset { IntOffset(fabOffset.x.roundToInt(), fabOffset.y.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume() // tránh propagate
                            fabOffset += dragAmount
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .padding(innerPadding)
                .background(Color(0xFF00D09E))
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                    .background(Color(0xFFF1FFF3))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(19.dp))
                    Text(
                        text = "How Can We Help You?",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(27.dp))
                    SupportToggle(
                        selectedOption = selected,
                        onOptionSelected = { selected = it }
                    )
                    Spacer(modifier = Modifier.height(9.dp))
                    StaticCategoryToggle()
                    Spacer(modifier = Modifier.height(9.dp))
                    if( (selected == "FAQ")){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF1FFF3))
                                .padding(16.dp)
                        ) {
                            items(faqList) { (question, answer) ->
                                FaqItem(question, answer)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }else {
                        Spacer(modifier = Modifier.height(20.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(Color(0xFFEFFFF2))
                        ) {
                            ContactOptionRow(
                                label = "Customer Service",
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.support),
                                        contentDescription = "Customer Service",
                                        tint = Color(0xFF093030),
                                        modifier = Modifier.size(25.dp)
                                    )
                                },
                                onClick = { navController.navigate(""
//                                    route = Screens.ChatBoxScreen.route
                                ) }
                            )
                            ContactOptionRow(
                                label = "Website",
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.website),
                                        contentDescription = "Website",
                                        tint = Color(0xFF093030),
                                        modifier = Modifier.size(25.dp)
                                    )
                                },
                                onClick = { /* Handle click */ }
                            )
                            ContactOptionRow(
                                label = "Facebook",
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.facebook),
                                        contentDescription = "Facebook",
                                        tint = Color(0xFF093030),
                                        modifier = Modifier.size(25.dp)
                                    )
                                },
                                onClick = { /* Handle click */ }
                            )
                            ContactOptionRow(
                                label = "Phone",
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.phone),
                                        contentDescription = "Phone",
                                        tint = Color(0xFF093030),
                                        modifier = Modifier.size(25.dp)
                                    )
                                },
                                onClick = { /* Handle click */ }
                            )
                            ContactOptionRow(
                                label = "Instagram",
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.insta),
                                        contentDescription = "Instagram",
                                        tint = Color(0xFF093030),
                                        modifier = Modifier.size(25.dp)
                                    )
                                },
                                onClick = { /* Handle click */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun HelpCPreview() {
    To_do_appTheme{
//        HelpCenterScreen()
    }
}

@Composable
fun SupportToggle(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.padding(start = 36.dp, end = 36.dp)
            .background(Color(0xFFDFF7E2),
                shape = RoundedCornerShape(18.dp))

    ) {
        // FAQ Box
        Box(
            modifier = Modifier
                .weight(1f).padding(6.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (selectedOption == "FAQ") Color(0xFF00D09C) else Color(0xFFDFF7E2)
                )
                .clickable { onOptionSelected("FAQ") },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "FAQ",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
            )
        }

        // Contact Us Box
        Box(
            modifier = Modifier
                .weight(1f).padding(6.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (selectedOption == "Contact") Color(0xFF00D09C) else Color(0xFFDFF7E2)
                )
                .clickable { onOptionSelected("Contact") },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Contact Us",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
            )
        }
    }
}
@Composable
fun StaticCategoryToggle() {
    val options = listOf("General", "Account", "Services")

    Row(
        modifier = Modifier.padding(start = 36.dp, end = 36.dp)
            .background(Color(0xFFDFF7E2), shape = RoundedCornerShape(8.dp))

    ) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(37.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFDFF7E2)), // All same background
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
            }
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1FFF3))
            .clickable { expanded = !expanded }
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.displayMedium.copy(
                    color = Color(0xFF093030),
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ), modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color(0xFF1A1A1A)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Text(
                text = "- ${answer}",
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 16.sp),
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun ContactOptionRow(label: String, icon: @Composable  ()->Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick()}
            .padding(vertical = 17.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp).clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF00B894)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Go",
            tint = Color.Black
        )
    }
}

val faqList = listOf(
    "How to use FinWise?" to "You can get started by signing up and linking your accounts...",
    "How much does it cost to use FinWise?" to "FinWise is free for basic features. Premium plans start at $5/month.",
    "How to contact support?" to "Reach out to us via the 'Help' section or email support@finwise.com.",
    "How can I reset my password if I forget it?" to "Tap on 'Forgot password' on the login screen and follow the steps.",
    "Are there any privacy or data security measures in place?" to "Yes, we use industry-standard encryption and follow GDPR.",
    "Can I customize settings within the application?" to "Yes, navigate to the Settings screen to adjust preferences.",
    "How can I delete my account?" to "Go to Settings > Account > Delete Account.",
    "How do I access my expense history?" to "Tap on 'Expenses' tab to see your full history by month/year.",
    "Can I use the app offline?" to "Yes, but some features may be limited without an internet connection."
)