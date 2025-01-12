package com.focussystems.volumecontrolviasms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast
import android.media.AudioManager

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle: Bundle? = intent.extras
            val pdus = bundle?.get("pdus") as Array<Any>?
            val messages = pdus?.map { pdu ->
                val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                smsMessage.messageBody
            }

            if (messages != null) {
                handleMessage(context, messages.joinToString(" "))
            }
        }
    }

    private fun handleMessage(context: Context?, message: String) {
        // Retrieve the authorized numbers from SharedPreferences
        val authorizedNumbers = getAuthorizedNumbers(context)
        val senderNumber = getSenderNumber() // Retrieve the sender's phone number

        if (authorizedNumbers.contains(senderNumber)) {
            // Process the message to adjust volume
            if (message.contains("Volume 100")) {
                setVolume(context, 100)
            } else if (message.contains("Volume 0")) {
                setVolume(context, 0)
            } else if (message.contains("Volume 50")) {
                setVolume(context, 50)
            } else if (message.contains("Volume Up")) {
                changeVolume(context, 5)
            }
        } else {
            Toast.makeText(context, "Unauthorized number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setVolume(context: Context?, volumeLevel: Int) {
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volume = (volumeLevel * maxVolume) / 100
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

    private fun changeVolume(context: Context?, increment: Int) {
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val newVolume = (currentVolume + increment).coerceIn(0, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
    }

    private fun getAuthorizedNumbers(context: Context?): Set<String> {
        val sharedPrefs = context?.getSharedPreferences("appPrefs", Context.MODE_PRIVATE)
        return sharedPrefs?.getStringSet("authorized_numbers", emptySet()) ?: emptySet()
    }

    private fun getSenderNumber(): String {
        // Ideally, extract sender number from the SMS.
        // The actual implementation may depend on the device and available API.
        return ""
    }
}
