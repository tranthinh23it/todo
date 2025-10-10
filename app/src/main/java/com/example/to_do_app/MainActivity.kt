package com.example.to_do_app

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.to_do_app.presentation.screens.groups.AddTeamMembersPage
import com.example.to_do_app.presentation.screens.groups.GroupTasksPage
import com.example.to_do_app.presentation.screens.HomePage
import com.example.to_do_app.presentation.screens.groups.KanbanBoardPage
import com.example.to_do_app.presentation.screens.authen.LoginPage
import com.example.to_do_app.presentation.screens.user.MyAccountPage
import com.example.to_do_app.presentation.screens.task.MyTaskPage
import com.example.to_do_app.presentation.screens.notification.NotificationsPage
import com.example.to_do_app.presentation.screens.user.PersonalInfoPage
import com.example.to_do_app.presentation.screens.authen.RegisterPage
import com.example.to_do_app.presentation.screens.StartPage
import com.example.to_do_app.presentation.screens.authen.ForgotPasswordPage
import com.example.to_do_app.presentation.screens.user.SettingPage
import com.example.to_do_app.services.NotificationBus
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.DeepLinkManager
import com.example.to_do_app.util.DeepLinkType
import com.example.to_do_app.util.Screens
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.to_do_app.presentation.screens.OnboardingScreen
import com.example.to_do_app.presentation.screens.user.CalendarPage
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermission()

        handleDeepLink(intent?.data)

        // Thiết lập UI
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )


        setContent {
            Log.d("MainActivity", "setContent called")
            To_do_appTheme {
                Log.d("MainActivity", "Theme applied")
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

//                    MyTaskPage(navController)// Khởi tạo NavController
//                    ToDoApp(navController = navController)
                    KanbanBoardPage(navController)
//                    StartPage(navController)
//                    OnboardingScreen(navController)

//                    GroupTasksPage(navController)
                    DeepLinkAndNotificationHandler(navController)

                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent.data)
    }

    private fun handleDeepLink(uri: Uri?) {
        if (uri != null) {
            DeepLinkManager.setPendingDeepLink(uri.toString())
            val deepLinkData = DeepLinkManager.parseDeepLink(uri.toString())
            Log.d("MainActivity", "Deep link received: $deepLinkData")
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

}

@Composable
fun DeepLinkAndNotificationHandler(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val deepLink = DeepLinkManager.getAndClearPendingDeepLink()
        deepLink?.let {
            val deepLinkData = DeepLinkManager.parseDeepLink(it)
            when (deepLinkData?.type) {
                DeepLinkType.TASK -> navController.navigate("kanban_board_page/${deepLinkData.id}")
                DeepLinkType.PROJECT -> {
                    navController.navigate(Screens.KanbanBoardPage.createRoute(deepLinkData.id))
                }
                DeepLinkType.NOTIFICATIONS -> {
                    navController.navigate(Screens.NotificationsPage.route)
                }
                DeepLinkType.HOME -> {
                    navController.navigate(Screens.HomePage.route)
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        NotificationBus.events.collect { event ->
            val message = event.data["message"] ?: "Bạn có thông báo mới!"
            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "Xem",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    // Người dùng bấm "Xem" → điều hướng
                    when (event.data["type"]) {
                        "task_assigned", "task_commented", "status_changed" -> {
                            val taskId = event.data["taskId"]
                            if (taskId != null) {
                                navController.navigate("kanban_board_page/$taskId")
                            }
                        }
                        "project_updated" -> {
                            val projectId = event.data["projectId"]
                            if (projectId != null) {
                                navController.navigate("kanban_board_page/$projectId")
                            }
                        }
                        else -> {
                            // fallback cho deep link
                            val deepLink = event.deepLink?.toString()
                            deepLink?.let {
                                val deepLinkData = DeepLinkManager.parseDeepLink(it)
                                when (deepLinkData?.type) {
                                    DeepLinkType.TASK -> navController.navigate("kanban_board_page/${deepLinkData.id}")
                                    DeepLinkType.PROJECT -> navController.navigate("kanban_board_page/${deepLinkData.id}")
                                    DeepLinkType.NOTIFICATIONS -> navController.navigate("notifications_page")
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Hiển thị snackbar
    SnackbarHost(hostState = snackbarHostState)
}


@Composable
fun ToDoApp(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.OnboardingPage.route) {
        composable(Screens.OnboardingPage.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screens.StartPage.route) {
            StartPage(navController = navController)
        }

        composable(Screens.LoginPage.route) {
            LoginPage(navController)
        }

        composable(Screens.RegisterPage.route) {
            RegisterPage(navController)
        }

        composable(Screens.KanbanBoardPage.route, arguments = listOf(navArgument("projectId") {
            type = NavType.StringType
        })) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            KanbanBoardPage(navController)  // Truyền projectId vào KanbanBoardPage
        }

        composable(Screens.AddTeamMembersPage.route, arguments = listOf(navArgument("projectId") {
            type = NavType.StringType  // Khai báo kiểu tham số projectId
        })) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            AddTeamMembersPage(navController)  // Truyền projectId vào AddTeamMembersPage
        }

        composable(Screens.HomePage.route) {
            HomePage(navController)
        }
        composable(Screens.NotificationsPage.route) {
            NotificationsPage(navController)
        }
        composable(Screens.MyAccountPage.route) {
            MyAccountPage(navController)
        }
        composable(Screens.MyTaskPage.route) {
            MyTaskPage(navController)
        }

        composable(Screens.GroupTasksPage.route) {
            GroupTasksPage(navController)
        }

        composable(Screens.PersonalInfoPage.route) {
            PersonalInfoPage(navController)
        }

        composable(Screens.SecurityPage.route){
            SettingPage(navController)
        }
         composable(Screens.CalenderTimePage.route){
             CalendarPage(navController = navController)
         }
        composable(Screens.ForgotPassWordPage.route){
            ForgotPasswordPage()
        }
//        composable("calendarPage"){
//            CalendarPage(navController = navController)
//        }
        composable(Screens.CalenderPage.route){
            CalendarPage(navController = navController)
        }

    }
}