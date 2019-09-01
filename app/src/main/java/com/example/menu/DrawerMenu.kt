package com.example.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.project.*
import com.example.session.SessionUser
import com.google.android.material.navigation.NavigationView

class DrawerMenu(
    val context: Context,
    val menu : NavigationView,
    val activity : Activity
) : NavigationView.OnNavigationItemSelectedListener {

    init{
        menu.setNavigationItemSelectedListener(this)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.navigation_account -> {
                context.startActivity(Intent(context, UserInfoActivity::class.java))
            }
            R.id.navigation_events -> {
                context.startActivity(Intent(context, EventActivity::class.java))
            }
            R.id.navigation_friends -> {
                context.startActivity(Intent(context, FriendsActivity::class.java))
            }
            R.id.navigation_notifications -> {
                context.startActivity(Intent(context, NotificationPushActivity::class.java))
            }
            R.id.navigation_settings -> {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            }
        }
        val drawerLayout: DrawerLayout = activity.findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private val session = SessionUser(context)


}