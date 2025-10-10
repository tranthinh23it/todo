package com.example.to_do_app.presentation.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int // Thay bằng @DrawableRes trong project thực tế
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    navController : NavController
) {
    val pages = listOf(
        OnboardingPage(
            title = "Stay Focused, One\nPomodoro at a Time",
            description = "Break your work into focused sessions with built-in breaks. Boost productivity, reduce burnout, and get more done—mindfully.",
            imageRes = 0 // R.drawable.onboarding_1
        ),
        OnboardingPage(
            title = "Tasks, Tags & Projects -\nAll in One Place",
            description = "Plan your work, organize tasks by tags and projects, and track your progress with ease. Stay structured, even on busy days.",
            imageRes = 0 // R.drawable.onboarding_2
        ),
        OnboardingPage(
            title = "Visualize Your Focus &\nGrowth",
            description = "See your productivity in calendar views and insightful reports. Reflect on your effort, celebrate your streaks, and stay motivated.",
            imageRes = 0 // R.drawable.onboarding_3
        )
    )

    val pagerState = rememberPagerState()
    val isLastPage = pagerState.currentPage == pages.size - 1
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
//            .background(Color(0xFFFF5252))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pager với các trang
            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            // Indicators
            PageIndicator(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Buttons
            OnboardingButtons(
                isLastPage = isLastPage,
                onSkip = {navController.navigate(Screens.StartPage.route)},
                onContinue = {
                    if (isLastPage) {
                        navController.navigate(Screens.StartPage.route)
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)
            )
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Phone mockup area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder cho phone mockup
            // Trong project thực tế, thay bằng Image với resource tương ứng
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .height(560.dp)
                    .background(Color.Green.copy(alpha = 0.1f), RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Phone\nMockup",
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 30.sp,
            ),
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = 16.sp,
            ),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            val width by animateDpAsState(
                targetValue = if (index == currentPage) 32.dp else 8.dp
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (index == currentPage) Color(0xFFFF5252)
                        else Color(0xFFFF5252).copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
fun OnboardingButtons(
    isLastPage: Boolean,
    onSkip: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Skip button
        if (!isLastPage) {
            Button(
                onClick = onSkip,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color(0xFFFF5252),
                    containerColor = Color(0xFFFF5252).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(28.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
//            Spacer(modifier = Modifier.weight(1f))
        }

        // Continue/Get Started button
        Button(
            onClick = onContinue,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color(0xFFFF5252)
            ),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(
                text = if (isLastPage) "Let's Get Started" else "Continue",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 16.sp,
                ),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Preview
@Preview
@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreenPreview() {
    To_do_appTheme {
//        OnboardingScreen()

    }
}