package com.focussystems.volumecontrolviasms

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import android.media.AudioManager
import android.os.Build
import android.provider.Settings

class SmsReceiver : BroadcastReceiver() {

    private val TAG = "SmsReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "SMS Received!")  // Log to check if receiver is triggered
        val sharedPrefs = context?.getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
        val isRemoteControlEnabled = sharedPrefs?.getBoolean("remote_control_enabled", false)

        // Early exit if remote control is disabled
        if (!isRemoteControlEnabled!!) {
            Log.d(TAG, "Remote control is disabled. Ignoring SMS command.")
            return  // Exit early
        }

        if (intent != null && intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle: Bundle? = intent.extras
            val pdus = bundle?.get("pdus") as Array<Any>?
            val messages = pdus?.map { pdu ->
                val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                smsMessage.messageBody
            }

            if (messages != null) {
                // Log the message received
                val incomingMessage = messages.joinToString(" ")
                Log.d(TAG, "Received SMS: $incomingMessage")

                // Process the message
                handleMessage(context, incomingMessage)
            }
        }
    }

    private fun handleMessage(context: Context?, message: String) {
        Log.d(TAG, "Processing message: $message")

        // Check if the message contains volume commands
        if (message.contains("Volume 100", ignoreCase = true)) {
            setVolume(context, 100)
        } else if (message.contains("Volume 0", ignoreCase = true)) {
            setVolume(context, 0)
        } else if (message.contains("Volume 25", ignoreCase = true)) {
            setVolume(context, 25)
        } else if (message.contains("Volume 50", ignoreCase = true)) {
            setVolume(context, 50)
        } else if (message.contains("Volume 75", ignoreCase = true)) {
            setVolume(context, 75)
        }
    }

    private fun setVolume(context: Context?, volumeLevel: Int) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check the current DND mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            } catch (e: SecurityException) {
                Log.e(TAG, "Error changing DND mode: ${e.message}")
                Toast.makeText(context, "Failed to change DND mode", Toast.LENGTH_SHORT).show()
            }
            Log.d(TAG, "DND mode set to off.")
        }

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        val volume = (volumeLevel * maxVolume) / 100
        audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, 0)
        Log.d(TAG, "Volume set to $volumeLevel%") // Log volume change
    }
}

