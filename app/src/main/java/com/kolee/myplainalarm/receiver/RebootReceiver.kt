package com.kolee.myplainalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


// 5
class RebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        context?.let { ctx ->
            // It is necessary to reSchedule the Alarms,
            // because the scheduled alarms is cancelled after rebooting.
            // RescheduleAlarmWorker.oneTimeWorker(ctx)
        }
    }
}