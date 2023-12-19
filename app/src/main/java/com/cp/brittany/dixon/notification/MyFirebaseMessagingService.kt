package com.cp.brittany.dixon.notification

import android.content.Intent
import android.util.Log
import com.cp.brittany.dixon.activities.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.PendingIntent
import java.util.*
import android.app.Notification
import android.media.RingtoneManager
import android.app.NotificationChannel
import android.os.Build
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.cp.brittany.dixon.R
import com.cp.brittany.dixon.activities.SplashActivity
import com.cp.brittany.dixon.base.BaseActivity
import com.cp.brittany.dixon.utils.Constants
import com.google.gson.Gson


class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val TAG: String = "MyFirebaseMessagingService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // ...

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")
        var title = ""
        var body = ""
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            title = remoteMessage.data["title"].toString()
            body = remoteMessage.data["body"].toString()
            //sendNotification(NotificationModel(title,body,"",""))
            val model = Gson().fromJson(remoteMessage.data["body"].toString(), NotificationModel::class.java)
            model.title = title
            sendNotification(model)
        }

        // Check if message contains a notification payload.
//        remoteMessage.notification?.let {
//            Log.d(TAG, "Message Notification Body: ${it.body}")
//            title = it.title.toString()
//            body = it.body.toString()
//            val model = Gson().fromJson(it.body.toString(), NotificationModel::class.java)
//            model.title = title
//            sendNotification(model)
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    private fun sendNotification(model: NotificationModel) {
        val myIntent = if (BaseActivity.isAppRunning){
            Intent(applicationContext,HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else{
            Intent(applicationContext,SplashActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        //myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        myIntent.putExtra("myData",model)
        val resultPendingIntent = PendingIntent.getActivity(applicationContext, Random().nextInt(), myIntent, 0)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notificationChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                Constants.channelName, Constants.channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = ""
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        // to display notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = mNotificationManager.getNotificationChannel(Constants.channelName)
            channel.canBypassDnd()
        }
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, Constants.channelName)
        notificationBuilder
            .setContentTitle(model.title)
            .setContentText(model.message)
            .setGroup(Constants.channelName)
            .setStyle(NotificationCompat.BigTextStyle())
            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setGroupSummary(true)
            .setContentIntent(resultPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationChannel != null) {
                notificationBuilder.setChannelId(Constants.channelName)
                mNotificationManager.createNotificationChannel(notificationChannel)
            }
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}