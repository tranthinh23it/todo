package com.example.to_do_app.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.to_do_app.R
import com.example.to_do_app.ui.theme.To_do_appTheme
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: Painter? = null,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    val textStyle = MaterialTheme.typography.displayMedium.copy(
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.monasan_sb))
    )

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        placeholder = {
            Text(
                label,
                style = textStyle,
                color = Color.Gray
            )
        },
        textStyle = textStyle,
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = label,
                    modifier = Modifier.size(20.dp)
                )
            } else if (isPassword) {
                Icon(
                    painter = painterResource(R.drawable.padlock),
                    contentDescription = "Password",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.visibility else R.drawable.visible),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
            focusedContainerColor = Color.Gray.copy(alpha = 0.1f)
        )
    )
}

// Giữ lại các hàm cũ để tương thích ngược
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: Painter,
    modifier: Modifier = Modifier
) {
    CustomInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        leadingIcon = leadingIcon,
        isPassword = false,
        modifier = modifier
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    CustomInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isPassword = true,
        modifier = modifier
    )
}

@Composable
fun ButtonSignUp(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)) // Bo góc trước
            .background(Color.White)
            .border(1.dp, Color.White, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onClick() },
            modifier = Modifier
                .height(48.dp)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(25.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB3022))
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb))
                ),
            )
        }
    }
}

@Composable
@Preview
fun ButtonPreview() {
    To_do_appTheme {
        ButtonSignUp("Sign Up", onClick = { /*TODO*/ }, modifier = Modifier)
    }
}


@Composable
fun CustomDropdownField(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String,
    leadingIcon: Painter? = null,
    modifier: Modifier = Modifier
) {
    val expanded = remember { mutableStateOf(false) }
    val textStyle = MaterialTheme.typography.displayMedium.copy(
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.monasan_sb))
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { expanded.value = true }
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = selectedOption.ifEmpty { label },
                style = textStyle,
                color = if (selectedOption.isEmpty()) Color.Gray else Color.Black,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(R.drawable.down),
                contentDescription = "Dropdown icon",
                modifier = Modifier.size(16.dp)
            )
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = textStyle) },
                    onClick = {
                        onOptionSelected(option)
                        expanded.value = false
                    }
                )
            }
        }
    }
}


@Composable
fun CustomDatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    label: String,
    leadingIcon: Painter? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Nếu đã có selectedDate, parse để hiện đúng ngày
    if (selectedDate.isNotEmpty()) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.time = sdf.parse(selectedDate) ?: Date()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val pickedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(pickedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val textStyle = MaterialTheme.typography.displayMedium.copy(
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.monasan_sb))
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = Color.Gray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { datePickerDialog.show() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = if (selectedDate.isEmpty()) label else selectedDate,
                style = textStyle,
                color = if (selectedDate.isEmpty()) Color.Gray else Color.Black,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select Date"
            )
        }
    }
}


@Composable
fun DateTimePickerField(
    selectedDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Biến tạm để giữ ngày được chọn từ DatePicker
    var tempDate by remember { mutableStateOf<LocalDateTime?>(null) }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, h.mm a", Locale.ENGLISH)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Gray.copy(alpha = 0.1f))
            .clickable { showDatePicker = true }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Time",
                modifier = Modifier.size(20.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = selectedDateTime.format(dateFormatter),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.monasan_sb)),
                    color = Color.Black
                )
            )
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Lưu ngày tạm
                tempDate = selectedDateTime
                    .withYear(year)
                    .withMonth(month + 1)
                    .withDayOfMonth(dayOfMonth)

                showDatePicker = false
                showTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showTimePicker && tempDate != null) {
        val currentHour = selectedDateTime.hour
        val currentMinute = selectedDateTime.minute

        TimePickerDialog(
            context,
            { _, hour, minute ->
                showTimePicker = false
                tempDate?.let { date ->
                    val updatedDateTime = date.withHour(hour).withMinute(minute)
                    onDateTimeSelected(updatedDateTime)
                }
            },
            currentHour,
            currentMinute,
            false
        ).show()
    }
}
