package com.example.strayanimaltracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.strayanimaltracker.NotificationUtils

class PostReceiver(var nome: String, var email: String): BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent) {
            NotificationUtils.notificationSimpleBroadcast(ctx, nome ,email)
    }

}
