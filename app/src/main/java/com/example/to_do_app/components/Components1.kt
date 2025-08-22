package com.example.to_do_app.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_app.R
import com.example.to_do_app.ui.theme.To_do_appTheme
import androidx.compose.ui.graphics.painter.Painter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTopAppBar(
    text: String, 
    onBackClick: () -> Unit, 
    icon: ImageVector? = null, 
    onClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_bold))
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = {onBackClick()}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (icon != null) {
                IconButton(onClick = { onClick() }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Action",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTopAppBar(
    text: String, 
    onBackClick: () -> Unit, 
    iconPainter: Painter? = null, 
    onClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_bold))
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = {onBackClick()}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (iconPainter != null) {
                IconButton(onClick = { onClick() }) {
                    Icon(
                        painter = iconPainter,
                        contentDescription = "Action",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black
        )
    )
}

fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(intent, "Chia sẻ với:")
    context.startActivity(chooser)
}

@Composable
@Preview
fun componentpreview(){
    To_do_appTheme {
        CategoryTopAppBar( "Product Detail", onBackClick = {

        }, icon = Icons.Default.Share, {
//            shareText( context,"Let check this product!")
        })
    }
}
