package net.perfect.tea.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class InstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sessionId = intent?.getStringExtra("session_id")
        val pkg = intent?.getStringExtra("package")

    }
}