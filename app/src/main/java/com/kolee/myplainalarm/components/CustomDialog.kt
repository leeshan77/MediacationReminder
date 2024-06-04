package com.kolee.myplainalarm.components


import androidx.annotation.RawRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import com.kolee.myplainalarm.R
import com.kolee.myplainalarm.ui.theme.DarkBackground
import com.kolee.myplainalarm.ui.theme.LightBackground

@Composable
fun CustomDialog(
    title: String? = null,
    message: String,
    enableCancel: Boolean = false,
    @RawRes lottieImages: Int? = null,
    onClick: (Int) -> Unit
) {
    val darkTheme: Boolean = isSystemInDarkTheme()
    val haptic = LocalHapticFeedback.current
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        Dialog(
            onDismissRequest = {}
        ) {
            (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.8f)

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (darkTheme) DarkBackground else LightBackground
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    lottieImages?.let { img ->
                    }

                    title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        var okay = stringResource(id = R.string.btn_ok)
                        if (enableCancel) {
                            okay = stringResource(id = R.string.title_yes)

                            CustomButton(
                                onclick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showDialog = false
                                    onClick.invoke(0)
                                },
                                text = stringResource(id = R.string.btn_no),
                                modifier = Modifier.weight(1f).padding(16.dp)
                            )
                        }
                        CustomButton(
                            onclick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showDialog = false
                                onClick.invoke(1)
                            },
                            text = okay,
                            modifier = Modifier.weight(1f).padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

