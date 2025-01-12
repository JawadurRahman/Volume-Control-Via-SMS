package com.focussystems.volumecontrolviasms

import android.os.Bundle
import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.focussystems.volumecontrolviasms.databinding.ActivityMainBinding
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var switchRemoteControl: Switch
    private val TAG = "VolumeControlSMSReceiver"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request SMS permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 1)
        }

        // Request Do Not Disturb permission if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if the app has the ACCESS_NOTIFICATION_POLICY permission
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (!notificationManager.isNotificationPolicyAccessGranted) {
                // Permission is not granted, ask the user to enable it
                Log.d(TAG, "DND permission not granted. Requesting permission.")
                Toast.makeText(
                    this,
                    "Please allow the app to access DND settings.",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }

        switchRemoteControl = findViewById(R.id.switchRemoteControl)

        // Load saved settings when the app starts
        loadSettings()

        // Save settings when the switch state is changed
        switchRemoteControl.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "Toggled!")
            saveSettings(isChecked)
            val message = if (isChecked) "Remote volume control enabled." else "Remote volume control disabled."
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("appPrefs", MODE_PRIVATE)
        val isRemoteControlEnabled = prefs.getBoolean("remote_control_enabled", false)
        switchRemoteControl.isChecked = isRemoteControlEnabled
    }

    private fun saveSettings(isChecked: Boolean) {
        val prefs = getSharedPreferences("appPrefs", MODE_PRIVATE)
        val editor = prefs.edit()

        // Save whether remote control is enabled
        editor.putBoolean("remote_control_enabled", isChecked)

        editor.apply() // Save the preferences
    }

    // Override onRequestPermissionsResult to handle the result of permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "SMS permission granted!")
            } else {
                Log.d(TAG, "SMS permission denied!")
            }
        }
    }

}