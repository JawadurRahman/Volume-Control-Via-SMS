package com.focussystems.volumecontrolviasms

import android.os.Bundle
import android.Manifest
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
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
//}