package com.kolee.myplainalarm

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.kolee.myplainalarm.components.AddReminderDialog
import com.kolee.myplainalarm.components.IgnoreBatteryOptimizations
import com.kolee.myplainalarm.model.MedicationReminder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (!isGranted) {
                // Handle the case where permission is not granted
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var reminders by remember {
        mutableStateOf(
            listOf(
                MedicationReminder("White Pill", "09:00 PM", "note 1"),
                MedicationReminder("Blue Pill", "09:30 PM", "note 2")
            )
        )
    }

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        //
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Beck",
                            modifier = Modifier
                                .size(48.dp)
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            tint = Color.White,
                        )
                    }
                },
                title = { Text(text = "Medication Reminder", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(139, 188, 62, 255),
                    titleContentColor = Color(255, 255, 255),
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0D575))
                    .padding(paddingValues)
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(reminders) { reminder ->
                        MedicationReminderItem(
                            reminder = reminder,
                            onDelete = {
                                reminders = reminders.toMutableList().apply { remove(reminder) }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )

    if (showDialog) {
        AddReminderDialog(
            onDismiss = { showDialog = false },
            onAdd = { name, time, note ->
                reminders = reminders + MedicationReminder(name, time, note)
                showDialog = false
            }
        )
    }

    // 5
    // After a long time is passed, the scheduled alarm can not be active,
    // because the battery optimization remove it.
    IgnoreBatteryOptimizations(activity = LocalContext.current as Activity)
}

@Composable
fun MedicationReminderItem(reminder: MedicationReminder, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = reminder.name, fontSize = 18.sp)
                Text(text = reminder.note, fontSize = 14.sp, color = Color.Gray)
            }
            Row {
                Text(text = reminder.time.toString(), fontSize = 18.sp, color = Color.Gray)
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
