package com.example.strokeDetectionSystemForRacketRports.components.events.viewmodel

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.strokeDetectionSystemForRacketRports.R
import com.example.strokeDetectionSystemForRacketRports.components.events.view.AllEventsFragment

/**
 * This class is a broadcastReceiver class for handling alarm notifications.
 *
 */
class AlarmNotification : BroadcastReceiver() {
    companion object{
        const val NOTIFICATION_ID = 1
    }

    /**
     * This override method is called when the broadcast is received.
     *
     */
    override fun onReceive(context: Context, p1: Intent?) {
        val eventName = p1?.getStringExtra("eventName")
        createSimpleNotification(context, eventName)
    }

    /**
     * Allows to create a simple notification.
     *
     */
    private fun createSimpleNotification(context: Context, eventName: String?) {
        val intent = Intent()

        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, flag)

        val notification = NotificationCompat.Builder(context, AllEventsFragment.MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(eventName)
            .setContentText("Tu evento empieza en breve")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}