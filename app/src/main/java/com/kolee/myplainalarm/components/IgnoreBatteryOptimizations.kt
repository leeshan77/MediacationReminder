package com.kolee.myplainalarm.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kolee.myplainalarm.R

@SuppressLint("ObsoleteSdkInt", "BatteryLife")
@Composable
fun IgnoreBatteryOptimizations(activity: Activity) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        return // Battery Optimization is only available since Android M

    val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
    val packName = activity.packageName
    val isWhiteListing = pm.isIgnoringBatteryOptimizations(packName)

    if (isWhiteListing.not()) {
        CustomDialog(
            title = stringResource(id = R.string.ignore_dialog_title),
            message = stringResource(id = R.string.ignore_dialog_text)
        ) { index ->
            if (index == 1) {
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).also {
                    it.data = Uri.parse("package:$packName")
                    activity.startActivity(it)
                }
            }
        }
    }
}
