package com.example.to_do_app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_app.util.NotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NotificationBadge(
    userId: String,
    modifier: Modifier = Modifier,
    notificationCount: StateFlow<Int> = MutableStateFlow(0)
) {
    val count by notificationCount.collectAsState()
    
    if (count > 0) {
        Box(
            modifier = modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Red),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}

@Composable
fun NotificationBadgeSmall(
    userId: String,
    modifier: Modifier = Modifier,
    notificationCount: StateFlow<Int> = MutableStateFlow(0)
) {
    val count by notificationCount.collectAsState()
    
    if (count > 0) {
        Box(
            modifier = modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.Red)
        )
    }
}
