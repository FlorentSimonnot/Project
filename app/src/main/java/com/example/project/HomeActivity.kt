package com.example.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.menu.DrawerMenu
import com.example.menu.MenuCustom
import com.example.session.SessionUser
import com.google.android.material.bottomnavigation.BottomNavigationView




class HomeActivity : AppCompatActivity(), HomeFragment.OnFragmentInteractionListener{
    private lateinit var fragmentContainer  : FrameLayout
    private lateinit var homeFragment : HomeFragment
    private val session = SessionUser(this)
    private lateinit var fragment : Fragment

    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragment : Fragment = HomeFragment()
                if(!supportFragmentManager.isDestroyed) {
                    loadFragment(fragment)
                    supportActionBar?.title = "Home"
                    return@OnNavigationItemSelectedListener true
                }
                return@OnNavigationItemSelectedListener false
            }
            R.id.navigation_map -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                val checkAccountIntent = Intent(this, ActivityInfoUser::class.java)
                this.startActivity(checkAccountIntent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                val fragment : Fragment = DiscussionFragment()
                if(!supportFragmentManager.isDestroyed) {
                    loadFragment(fragment)
                    supportActionBar?.title = "Discussions"
                    return@OnNavigationItemSelectedListener true
                }
                return@OnNavigationItemSelectedListener false
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"

        if (savedInstanceState != null) {
            fragment = supportFragmentManager.getFragment(savedInstanceState, "TON_FRAGMENT") as Fragment
        } else {
            fragment = supportFragmentManager.findFragmentById(R.id.HomeFragment) as Fragment
        }

        fragmentContainer = findViewById(R.id.HomeFragment)
        homeFragment = HomeFragment()


        if (!session.isLogin()) {
            val logInIntent = Intent(this, LoginActivity::class.java)
            //Flags allow to block come back
            logInIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.finish()
            startActivity(logInIntent)
        }else {

            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            val navView: NavigationView = findViewById(R.id.nav_view)
            val bottomView : BottomNavigationView = findViewById(R.id.nav_bottom)

            val activity = this@HomeActivity
            val toggle = ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            //navView.setNavigationItemSelectedListener(this)
            val drawerMenu = DrawerMenu(this, navView, this@HomeActivity)
            val bottomMenu = MenuCustom(this, bottomView, this@HomeActivity, onNavigationItemSelectedListener)

            loadFragment(HomeFragment())
        }

    }

    fun loadFragment(fragment: Fragment){
        if(!isFinishing) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(this@HomeActivity, SearchBarActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        if (fragment != null) {
            supportFragmentManager.putFragment(outState, "TON_FRAGMENT", fragment)
        }
        super.onSaveInstanceState(outState)
    }

}
