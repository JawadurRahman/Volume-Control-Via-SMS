package com.focussystems.volumecontrolviasms

import android.os.Bundle
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
import android.widget.Button
import android.widget.EditText
import android.widget.Switch

class MainActivity : AppCompatActivity() {

    private lateinit var switchRemoteControl: Switch
    private lateinit var authorizedNumbersEditText: EditText
    private lateinit var buttonSaveSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchRemoteControl = findViewById(R.id.switchRemoteControl)
        authorizedNumbersEditText = findViewById(R.id.authorizedNumbersEditText)
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings)

        // Load saved settings when the app starts
        loadSettings()

        buttonSaveSettings.setOnClickListener {
            // Save settings when the "Save Settings" button is clicked
            val prefs = getSharedPreferences("appPrefs", MODE_PRIVATE)
            val editor = prefs.edit()

            // Save whether remote control is enabled
            editor.putBoolean("remote_control_enabled", switchRemoteControl.isChecked)

            // Save authorized numbers
            val numbers = authorizedNumbersEditText.text.toString().split(",").map { it.trim() }
            editor.putStringSet("authorized_numbers", numbers.toSet())

            editor.apply()
        }
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("appPrefs", MODE_PRIVATE)
        val isRemoteControlEnabled = prefs.getBoolean("remote_control_enabled", false)
        val authorizedNumbers = prefs.getStringSet("authorized_numbers", emptySet())?.joinToString(", ")

        switchRemoteControl.isChecked = isRemoteControlEnabled
        authorizedNumbersEditText.setText(authorizedNumbers)
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