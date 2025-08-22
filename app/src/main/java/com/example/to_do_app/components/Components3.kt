package com.example.to_do_app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.to_do_app.R
import com.example.to_do_app.ui.theme.To_do_appTheme


@Composable
fun DeleteTaskDialog2(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Delete Task",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0E3E3E),
                        fontFamily = FontFamily(Font(R.font.monasan_sb)),
                        )
                )
                Spacer(modifier = Modifier.height(11.dp))
                Text(
                    text = "Are you sure you want to delete this task?",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = FontFamily(Font(R.font.monasan_sb)),
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00C490), // Custom green
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    Text(
                        "Yes, Delete", style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 15.sp,
                            color = Color(0xFF093030), fontFamily = FontFamily(Font(R.font.monasan_sb)),

                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDFF7E2), // Custom green
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    Text(
                        "Cancel", style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 15.sp,
                            color = Color(0xFF093030), fontFamily = FontFamily(Font(R.font.monasan_sb)),
                        )
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun LogoutDialogPreview() {
    To_do_appTheme {
        DeleteTaskDialog2 (onDismiss = {}, onConfirm = {})
    }
}
