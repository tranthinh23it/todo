import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.to_do_app.R
import com.example.to_do_app.util.Screens
import com.example.to_do_app.components.NotificationBadge
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun BottomNavigation() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)) // Bo góc trước
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var isSelected by remember { mutableStateOf(false) }

        BottomNavItem( icon = painterResource(R.drawable.home), isSelected = true, {})
        BottomNavItem( icon =  painterResource(R.drawable.task), isSelected = false, {})
        BottomNavItem( icon =  painterResource(R.drawable.team), isSelected = false, {})
        BottomNavItem(

            icon =  painterResource(R.drawable.notification),
            isSelected = false,
            {})
        BottomNavItem( icon =  painterResource(R.drawable.user), isSelected = false, {})

    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    fun isSelected(route: String) =
        currentDestination?.hierarchy?.any { it.route == route } == true

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        fun smartNavigate(route: String) {
            if (!isSelected(route)) {
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                        inclusive = false
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
        Spacer(modifier = Modifier.padding(start = 15.dp))

        BottomNavItem(
            icon = painterResource(R.drawable.home),
            isSelected = isSelected(Screens.HomePage.route),
            onClick = { smartNavigate(Screens.HomePage.route) }
        )

        BottomNavItem(
            icon = painterResource(R.drawable.task),
            isSelected = isSelected(Screens.MyTaskPage.route),
            onClick = { smartNavigate(Screens.MyTaskPage.route) }
        )

        BottomNavItem(
            icon = painterResource(R.drawable.team),
            isSelected = isSelected(Screens.GroupTasksPage.route),
            onClick = { smartNavigate(Screens.GroupTasksPage.route) }
        )

        BottomNavItem(
            icon = painterResource(R.drawable.notification),
            isSelected = isSelected(Screens.NotificationsPage.route),
            onClick = { smartNavigate(Screens.NotificationsPage.route) },
            notificationCount = MutableStateFlow(0) // TODO: Implement real notification count
        )

        BottomNavItem(
            icon = painterResource(R.drawable.user),
            isSelected = isSelected(Screens.MyAccountPage.route),
            onClick = { smartNavigate(Screens.MyAccountPage.route) }
        )

        Spacer(modifier = Modifier.padding(end = 15.dp))
    }
}


@Composable
fun BottomNavItem(
    icon: Painter,
    isSelected: Boolean,
    onClick: () -> Unit,
    notificationCount: kotlinx.coroutines.flow.StateFlow<Int> = MutableStateFlow(0)
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp) // kích thước vùng nền
                .background(
                    color = if (isSelected) Color(0xFF5B5EF4) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            
            // Notification badge for notification icon
            if (icon == painterResource(R.drawable.notification)) {
                NotificationBadge(
                    userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    modifier = Modifier.align(Alignment.TopEnd),
                    notificationCount = notificationCount
                )
            }
        }
    }
}


@Composable
@Preview
fun BottomNavigationPreview() {

    To_do_appTheme {
        BottomNavigation()
    }
}