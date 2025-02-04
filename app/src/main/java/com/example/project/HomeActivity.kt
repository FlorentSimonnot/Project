package com.example.project

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.menu.DrawerMenu
import com.example.menu.MenuCustom
import com.example.session.SessionUser
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity(), HomeFragment.OnFragmentInteractionListener, View.OnClickListener{
    private lateinit var fragmentContainer  : FrameLayout
    private val session = SessionUser(this)
    private lateinit var buttonLogOut : Button
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var bottomMenu : MenuCustom
    private lateinit var createEventButton : FloatingActionButton


    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener  { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragment : Fragment = HomeFragment(bottomMenu)
                if(!supportFragmentManager.isDestroyed) {
                    loadFragment(fragment)
                    supportActionBar?.title = getString(R.string.home_title)
                    return@OnNavigationItemSelectedListener true
                }
                return@OnNavigationItemSelectedListener false
            }
            R.id.navigation_map -> {
                val fragment : Fragment = MapFragment(bottomMenu)
                if(!supportFragmentManager.isDestroyed) {
                    loadFragment(fragment)
                    supportActionBar?.title = getString(R.string.map_title)
                    return@OnNavigationItemSelectedListener true
                }
                return@OnNavigationItemSelectedListener false
            }
            R.id.navigation_notifications -> {
                val fragment : Fragment = NotificationsFragment(bottomMenu)
                if(!supportFragmentManager.isDestroyed) {
                    loadFragment(fragment)
                    supportActionBar?.title = getString(R.string.notifications_title)
                    return@OnNavigationItemSelectedListener true
                }
                return@OnNavigationItemSelectedListener false
            }
            R.id.navigation_chat -> {
                val fragment : Fragment = DiscussionFragment()
                if(!supportFragmentManager.isDestroyed) {
                    loadFragment(fragment)
                    supportActionBar?.title = getString(R.string.discussions_title)
                    return@OnNavigationItemSelectedListener true
                }
                return@OnNavigationItemSelectedListener false
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        session.getNightMode()

        if (!session.isLogin()) {
            val logInIntent = Intent(this, LoginActivity::class.java)
            logInIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            this.startActivity(logInIntent)
            this.overridePendingTransition(0, 0)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.home_title)


        fragmentContainer = findViewById(R.id.HomeFragment)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        //Verify if user has already seen the tutorial for beginner
        FirebaseDatabase.getInstance().getReference("parameters/${session.getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(!p0.hasChild("tutorialBeginner")){
                    val intent = Intent(this@HomeActivity, Tutorial::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }

        })

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val bottomView : BottomNavigationView  = findViewById(R.id.nav_bottom)
        createEventButton = findViewById(R.id.fab)
        buttonLogOut = findViewById(R.id.logout)
        buttonLogOut.setOnClickListener(this)
        createEventButton.setOnClickListener(this)

        val activity = this@HomeActivity
        val toggle = ActionBarDrawerToggle(
            activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val drawerMenu = DrawerMenu(this@HomeActivity, navView, this@HomeActivity)
        bottomMenu = MenuCustom(this@HomeActivity, bottomView, this@HomeActivity, onNavigationItemSelectedListener)

        drawerMenu.setInfo()

        loadFragment(HomeFragment(bottomMenu))

        //}

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.logout -> {
                val logOutAlert = AlertDialog.Builder(this)
                logOutAlert.setTitle(getString(R.string.log_out_dialog_title))
                logOutAlert.setPositiveButton(getString(R.string.log_out_dialog_yes)){ _, _ ->
                    session.signOut()
                    googleSignInClient.signOut()
                    LoginManager.getInstance().logOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    finish()
                    startActivity(intent)
                }
                logOutAlert.setNegativeButton(getString(R.string.log_out_dialog_no)){ _, _ ->

                }
                logOutAlert.show()

            }
            R.id.fab -> {
                startActivity(Intent(this@HomeActivity, CreateEventActivity::class.java))
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        if(!isFinishing) {
            fragment.retainInstance = true
            supportFragmentManager.beginTransaction().replace(R.id.HomeFragment, fragment).commit()
        }
    }

    override fun onFragmentInteraction(sendBackText: String) {
        //Bruh
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }


}
