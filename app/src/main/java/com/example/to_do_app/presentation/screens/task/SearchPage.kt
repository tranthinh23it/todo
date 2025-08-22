package com.example.to_do_app.presentation.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_app.ui.theme.To_do_appTheme

@Composable
fun SearchPage() {
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    
    val recentSearches = listOf(
        "E-Commerce App",
        "Market Analysis", 
        "User Interface",
        "Brainstorming",
        "Web Design",
        "User Experience",
        "Low Fidelity",
        "Design Wireframe"
    )
    
    val keyboardRows = listOf(
        listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
        listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
        listOf("Z", "X", "C", "V", "B", "N", "M")
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        SearchTopBar(
            searchText = searchText,
            onSearchTextChange = { 
                searchText = it
                isSearching = it.isNotEmpty()
            },
            onBackClick = { /* TODO: Navigate back */ },
            onClearClick = { 
                searchText = ""
                isSearching = false
            }
        )
        
        if (isSearching) {
            // Search Results
            SearchResults(searchText = searchText)
        } else {
            // Recent Searches + Keyboard
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Recent Searches
                RecentSearches(
                    searches = recentSearches,
                    onSearchClick = { search ->
                        searchText = search
                        isSearching = true
                    },
                    onRemoveClick = { /* TODO: Remove search */ }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Virtual Keyboard
//                VirtualKeyboard(
//                    keyboardRows = keyboardRows,
//                    onKeyClick = { key ->
//                        searchText += key.lowercase()
//                    },
//                    onSpaceClick = {
//                        searchText += " "
//                    },
//                    onGoClick = {
//                        isSearching = true
//                    }
//                )
            }
        }
    }
}

@Composable
fun SearchTopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
        
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { 
                Text(
                    text = if (searchText.isEmpty()) "Search..." else searchText,
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = onClearClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF5B5EF4)
            )
        )
    }
}

@Composable
fun RecentSearches(
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            IconButton(onClick = { /* TODO: Clear all */ }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear All",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(searches) { search ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSearchClick(search) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = search,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    IconButton(
                        onClick = { onRemoveClick(search) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }
                }
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}

//@Composable
//fun VirtualKeyboard(
//    keyboardRows: List<List<String>>,
//    onKeyClick: (String) -> Unit,
//    onSpaceClick: () -> Unit,
//    onGoClick: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color(0xFFF5F5F5))
//            .padding(8.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        keyboardRows.forEach { row ->
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
//            ) {
//                row.forEach { key ->
//                    Button(
//                        onClick = { onKeyClick(key) },
//                        modifier = Modifier.size(32.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.White,
//                            contentColor = Color.Black
//                        ),
//                        contentPadding = PaddingValues(0.dp)
//                    ) {
//                        Text(
//                            text = key,
//                            fontSize = 16.sp
//                        )
//                    }
//                }
//            }
//        }
//
//        // Bottom row with special keys
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
//        ) {
//            Button(
//                onClick = { /* TODO: Numbers */ },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Gray,
//                    contentColor = Color.White
//                )
//            ) {
//                Text("123")
//            }
//
//            Button(
//                onClick = onSpaceClick,
//                modifier = Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.White,
//                    contentColor = Color.Black
//                )
//            ) {
//                Text("space")
//            }
//
//            Button(
//                onClick = onGoClick,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF007AFF),
//                    contentColor = Color.White
//                )
//            ) {
//                Text("Go")
//            }
//        }
//
//        // Bottom icons
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Icon(
//                imageVector = Icons.Default.Face,
//                contentDescription = "Emoji",
//                tint = Color.Gray
//            )
//            Icon(
//                imageVector = Icons.Default.MoreVert,
//                contentDescription = "Voice",
//                tint = Color.Gray
//            )
//        }
//    }
//}

@Composable
fun SearchResults(searchText: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Not Found Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Color(0xFFFF4040),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Not Found",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Not Found",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Please try searching with other keywords",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchPagePreview() {
    To_do_appTheme {
        SearchPage()
    }
}
