package com.example.to_do_app

import CalendarPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.to_do_app.presentation.screens.groups.AddTeamMembersPage
import com.example.to_do_app.presentation.screens.groups.GroupProjectPage
import com.example.to_do_app.presentation.screens.HomePage
import com.example.to_do_app.presentation.screens.groups.KanbanBoardPage
import com.example.to_do_app.presentation.screens.authen.LoginPage
import com.example.to_do_app.presentation.screens.user.MyAccountPage
import com.example.to_do_app.presentation.screens.task.MyTaskPage
import com.example.to_do_app.presentation.screens.notification.NotificationsPage
import com.example.to_do_app.presentation.screens.user.PersonalInfoPage
import com.example.to_do_app.presentation.screens.authen.RegisterPage
import com.example.to_do_app.presentation.screens.StartPage
import com.example.to_do_app.presentation.screens.user.SettingPage
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.Screens
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


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
            To_do_appTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController() // Khởi tạo NavController

//                    ToDoApp(navController = navController)
//                    MyTaskPage(navController)
//                    SettingPage(navController)
//                    MyAccountPage(navController)
                    CalendarPage(navController = navController)
                }
            }
        }
    }
}


@Composable
fun ToDoApp(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.LoginPage.route) {
        composable(Screens.StartPage.route) {
            StartPage()
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
            GroupProjectPage(navController)
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
        //        composable(Screens.CreateNewTaskPage.route, arguments = listOf(navArgument("projectId"){
        //            type = NavType.StringType
        //        })){
        //            CreateNewTaskBottomSheet(navController = navController, onDismiss = {}, showSheet = true)
        //        }

//            composable(Screens.AddTeamMembersPage.route, arguments = listOf(
//                navArgument("projectId") {
//                    type = NavType.StringType  // Khai báo kiểu tham số projectId
//                }
//            )) { backStackEntry ->
//                // Lấy projectId từ arguments
//                val projectId = backStackEntry.arguments?.getString("projectId")
//
//                // Truyền projectId vào AddTeamMembersPage
//                AddTeamMembersPage(navController)
//            }

    }
}