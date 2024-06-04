package com.kolee.myplainalarm.components

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.kolee.myplainalarm.receiver.ReminderReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var note by remember { mutableStateOf(TextFieldValue()) }
    var time by remember { mutableStateOf(Calendar.getInstance().time) }

    val context = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                time = calendar.time
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Medication Reminder") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medication Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.clickable(onClick = { showTimePicker = true })
                        .border(BorderStroke(1.dp, Color.Gray))
                        .padding(34.dp),
                    text = "Selected Time: ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(time)}",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 18.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Button(onClick = {

                val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(time)
                onAdd(name.text, formattedTime, note.text)

                // Schedule notification
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra("name", name.text)
                    putExtra("note", note.text)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    System.currentTimeMillis().toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // 6
                // After Android API 31, the permission "Use Exact Alarm" is necessary
                if (!checkAlarmPermission(context)) launchAlarmPermission(context)
                else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        time.time,
                        pendingIntent
                    )
                }

                onDismiss()
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun checkAlarmPermission(context: Context): Boolean{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
        return alarmManager?.canScheduleExactAlarms() ?: false
    }
    return true
}

private fun launchAlarmPermission(context: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Intent().also { intent ->
            intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            context.startActivity(intent)
        }
    }
}