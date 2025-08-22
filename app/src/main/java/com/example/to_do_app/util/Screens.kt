package com.example.to_do_app.util

sealed class Screens(val route : String){
    object StartPage : Screens("start_page")
    object CalenderTimePage : Screens("calender_time_page")
    object HomePage : Screens("home_page")
    object KanbanBoardPage : Screens("kanban_board_page/{projectId}"){
        fun createRoute(projectId : String) = "kanban_board_page/$projectId"
    }
//    object CreateNewTaskPage : Screens("create_new_task_page/{projectId}"){
//        fun createRoute(projectId : String) = "create_new_task_page/$projectId"
//    }
    object AddTeamMembersPage : Screens("add_team_members_page/{projectId}"){
        fun createRoute(projectId : String) = "add_team_members_page/$projectId"
    }


    object GroupTasksPage : Screens("group_tasks_page")
    object MyAccountPage : Screens("my_account_page")
    object MyTaskPage : Screens("my_task_page")
    object TaskDetailsPage : Screens("task_details_page")

    object NotificationsPage : Screens("notifications_page")
    object SettingsPage : Screens("settings_page")

    object LoginPage : Screens("login_page")
    object RegisterPage : Screens("register_page")

    object ReportPage : Screens("report_page")

    object PersonalInfoPage : Screens("personal_info_page")

    object SearchPage : Screens("search_page")

    object ForgotPassWordPage : Screens("forgot_password_page")

    object SecurityPage : Screens("security_page")

}