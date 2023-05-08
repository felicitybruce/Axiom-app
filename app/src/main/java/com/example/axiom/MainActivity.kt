package com.example.axiom

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity(), FragmentNavigation {

    private lateinit var navController: NavController

    companion object {
        private const val REQUEST_CODE_PERMISSION = 1
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Set up the action bar for use with the NavController
        NavigationUI.setupActionBarWithNavController(this, navController)

        // Call the sendNotification function when a button is clicked
        val button = findViewById<ImageButton>(R.id.btnNotification)
        button.setOnClickListener {
            sendNotification(this, "Axiom", "What are you waiting for? Curate your first blog now! \uD83E\uDEB6")
        }
    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
    }

    // MAIN CODE


    private fun sendNotification(context: Context, title: String, message: String) {
        // Check for permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_PERMISSION)
            return
        }

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("my_channel", "My Channel", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val builder = NotificationCompat.Builder(this, "my_channel")
            .setSmallIcon(R.drawable.ic_feather)
            .setContentTitle("Axiom") // Notification title
            .setContentText("What are you waiting for? Curate your first blog now 🪶.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        // Show the notification
        val notificationId = 1 // Unique notification ID
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, builder.build())
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
