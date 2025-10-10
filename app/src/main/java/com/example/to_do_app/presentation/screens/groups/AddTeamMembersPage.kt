package com.example.to_do_app.presentation.screens.groups

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_app.R
import com.example.to_do_app.components.ButtonSignUp
import com.example.to_do_app.domain.Task
import com.example.to_do_app.domain.User
import com.example.to_do_app.presentation.viewmodels.AuthViewModel
import com.example.to_do_app.presentation.viewmodels.ProjectViewModel
import com.example.to_do_app.presentation.viewmodels.TeamViewModel
import com.example.to_do_app.ui.theme.To_do_appTheme
import com.example.to_do_app.util.TaskPriority
import com.example.to_do_app.util.TaskStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

@Composable
fun AddTeamMembersPage(
    navController: NavController,
    authVM: AuthViewModel = viewModel(),
    projectVM : ProjectViewModel = viewModel()
) {
    val projectId = navController.currentBackStackEntry?.arguments?.getString("projectId")
    val currentUser by authVM.currentUser.observeAsState()
    LaunchedEffect(Unit) {
        authVM.fetchAndSetCurrentUser()
    }
//    val listMembers by authVM.usersByTeam.collectAsState()
//    LaunchedEffect(String) {
////        authVM.getUserByTeamId(currentUser?.team ?: "")
////    }
    val project by projectVM.selectedProject.collectAsState()
    LaunchedEffect(String) {
        projectVM.getProjectById(projectId?:"")
    }
    val listMembers = remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(project?.members) {
        project?.members?.let { members ->
            val users = members.map { memberId ->
                async { authVM.getUserById(memberId) } // suspend function
            }.awaitAll()
            listMembers.value = users.filterNotNull()
        }
    }

    val selectedMembers = remember { mutableStateListOf<User>() }
    Log.d("AddTeamMembersPage", "Selected Members: $selectedMembers")
    var searchText by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        // Search and Filter
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 16.dp),
            placeholder = {
                Text(
                    "Search tasks...",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            textStyle = MaterialTheme.typography.displayMedium.copy(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.monasan_sb))
            ),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF5B5EF4)
            ),

            )

        Spacer(modifier = Modifier.height(16.dp))

        if (listMembers.value.isEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 4.dp, end = 4.dp)
            ) {
                items(sampleUsers) { user ->
                    MemberTask(
                        user = user,
                        onMemberSelected = { isSelected ->
                            if (isSelected) {
                                if (user !in selectedMembers) selectedMembers.add(user)
                                Log.d("AddTeamMembersPage", "Added: ${user.name}")
                                Log.d("AddTeamMembersPage", "Selected Members: $selectedMembers")
                            } else {
                                selectedMembers.remove(user)
                                Log.d("AddTeamMembersPage", "Removed: ${user.name}")
                            }
                        }
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 4.dp, end = 4.dp)
            ) {
                items(listMembers.value) { user ->
                    MemberTask(
                        user = user,
                        onMemberSelected = { isSelected ->
                            if (isSelected) {
                                if (user !in selectedMembers) selectedMembers.add(user)
                            } else {
                                selectedMembers.remove(user)
                            }
                        }
                    )
                }
            }
            Log.d("AddTeamMembersPage", "Selected Members: $selectedMembers")
        }
        Spacer(modifier = Modifier.weight(1f))
        ButtonSignUp("Confirm", onClick = {
            val ids = ArrayList(selectedMembers.map { it.userId }) // đổi .id theo model của bạn
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("selected_members_ids", ids)

            Log.d("AddTeamMembersPage", "Send ids to previous page: $ids")
            navController.popBackStack()
        }, modifier = Modifier)

    }
}

val dummyTasks = List(3) { index ->
    Task(
        id = index.toString(),
        title = "Task $index",
        description = "Description for task $index",
        project = "Project $index",
        assignee = listOf("user_123", "user_456"),
        creator = "Creator $index",
        status = TaskStatus.PENDING,
        priority = TaskPriority.MEDIUM
    )
}

@Composable
@Preview
fun addPreview() {
    To_do_appTheme {
//        AddTeamMembersPage("")
    }
}

@Composable
fun MemberTask(
    user: User,
    onMemberSelected: (Boolean) -> Unit,
    teamViewModel: TeamViewModel = viewModel()
) {

    val team by teamViewModel.selectedTeam.collectAsState()
    LaunchedEffect(String) {
        teamViewModel.getTeamById(user.team)
    }

    var selectedMember by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = if (selectedMember) true else false,
                onCheckedChange = {
                    selectedMember = it
                    onMemberSelected(it) // Gọi callback khi checkbox thay đổi
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF5B5EF4),
                    uncheckedColor = Color.Gray
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.monasan_sb))
                    ),
                )

                if (team != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${team?.name} member",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.monasan_sb))
                        ),
                        color = Color.Gray
                    )
                }
            }

            // Priority and Assigned Person
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (user.imgUrl != null) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(kanbanMemberColors[1 % kanbanMemberColors.size])
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = kanbanMemberInitials[1 % kanbanMemberInitials.size],
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 10.sp,
                                fontFamily = FontFamily(Font(R.font.monasan_sb))
                            ),
                        )
                    }
                }
            }
        }
    }
}

val sampleUsers = listOf(
    User(
        name = "Nguyễn Văn A",
        userName = "nguyenvana",
        userId = "user_001",
        email = "vana@example.com",
        password = "password123",
        phoneNumber = "0912345678",
        dob = "1995-04-21",
        address = "123 Nguyễn Trãi, Hà Nội",
        imgUrl = "https://example.com/images/user_001.jpg",
        fcmToken = "fcm_token_001",
        gender = "Nam",
        team = "Frontend"
    ),
    User(
        name = "Trần Thị B",
        userName = "tranthib",
        userId = "user_002",
        email = "thib@example.com",
        password = "securepass456",
        phoneNumber = "0908765432",
        dob = "1998-08-12",
        address = "456 Lê Lợi, TP.HCM",
        imgUrl = "https://example.com/images/user_002.jpg",
        fcmToken = "fcm_token_002",
        gender = "Nữ",
        team = "Backend"
    ),
    User(
        name = "Lê Văn C",
        userName = "levanc",
        userId = "user_003",
        email = "vanc@example.com",
        password = "pass789",
        phoneNumber = "0987123456",
        dob = "1990-01-05",
        address = "789 Trần Hưng Đạo, Đà Nẵng",
        imgUrl = "https://example.com/images/user_003.jpg",
        fcmToken = "fcm_token_003",
        gender = "Nam",
        team = "Mobile"
    )
)
