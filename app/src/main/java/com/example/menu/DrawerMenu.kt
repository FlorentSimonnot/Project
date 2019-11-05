package com.example.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.size
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
        for(i in 0 until menu.menu.size){
            menu.menu[i].isCheckable = false
        }
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
                activity::finish
            }
            R.id.navigation_contact_us -> {
                context.startActivity(Intent(context, AboutUsActivity::class.java))
            }
        }
        val drawerLayout: DrawerLayout = activity.findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private val session = SessionUser(context)

    fun setInfo(){
        val headerMenu : View = menu.getHeaderView(0)
        val avatar : ImageView = headerMenu.findViewById(R.id.profile_photo)
        val identity : TextView = headerMenu.findViewById(R.id.identity)
        session.showPhotoUser(context, avatar)
        session.writeInfoUser(context, session.getIdFromUser(), identity, "identity")
    }
}