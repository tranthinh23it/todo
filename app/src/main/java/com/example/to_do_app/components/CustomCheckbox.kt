package com.example.to_do_app.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedColor: Color = Color(0xFFFF4040),
    uncheckedColor: Color = Color.LightGray,
    checkmarkColor: Color = Color.White,
    cornerRadius: Int = 1
) {
    val transition = updateTransition(targetState = checked, label = "checkbox_transition")
    val scale by transition.animateFloat(
        label = "checkbox_scale",
        transitionSpec = { tween(durationMillis = 100) }
    ) { isChecked -> if (isChecked) 1f else 0f }
    
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(RoundedCornerShape(cornerRadius.dp))
            .border(
                width = 2.dp,
                color = if (checked) checkedColor else uncheckedColor,
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .background(
                color = if (checked) checkedColor else Color.Transparent,
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = checkmarkColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}