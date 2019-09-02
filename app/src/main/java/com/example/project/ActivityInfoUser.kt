package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.menu.MenuCustom
import com.example.session.SessionUser
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActivityInfoUser : AppCompatActivity(), View.OnClickListener {
    private val session : SessionUser = SessionUser(this)
    private lateinit var info : LinearLayout
    private lateinit var deconnexion : LinearLayout
    private lateinit var friends : LinearLayout
    private lateinit var events : LinearLayout
    private lateinit var bottomMenu : MenuCustom

    private lateinit var googleSignInClient : GoogleSignInClient

    val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragment : Fragment = HomeFragment(bottomMenu)
                if(!supportFragmentManager.isDestroyed) {
                    loadFragment(fragment)
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
                val checkAccountIntent = Intent(this, MessagerieActivity::class.java)
                this.startActivity(checkAccountIntent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_user)

        val bottomView : BottomNavigationView = findViewById(R.id.nav_view)
        bottomMenu = MenuCustom(this, bottomView, this@ActivityInfoUser, onNavigationItemSelectedListener)

        val nameAndFirstnameTextView = findViewById<TextView>(R.id.name_and_firstName_account)
        val photoImageView = findViewById<ImageView>(R.id.profile_photo)

        val friendsNumber = findViewById<TextView>(R.id.friends_number)
        val createdNumber = findViewById<TextView>(R.id.created_number)
        val joinedNumber = findViewById<TextView>(R.id.joined_number)
        val notifications = findViewById<LinearLayout>(R.id.notificationPush)
        val share = findViewById<LinearLayout>(R.id.share_application)

        session.countFriends(friendsNumber)
        session.countCreated(createdNumber)
        session.countJoined(joinedNumber)

        info = findViewById(R.id.infos)
        deconnexion = findViewById(R.id.deconnexion)
        friends = findViewById(R.id.friends_layout)
        events = findViewById(R.id.events_layout)
        info.setOnClickListener(this)
        deconnexion.setOnClickListener(this)
        friends.setOnClickListener(this)
        events.setOnClickListener(this)
        notifications.setOnClickListener(this)
        share.setOnClickListener(this)

        /* GOOGLE LOGOUT */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        nameAndFirstnameTextView.text = session.identity(
            session.getIdFromUser(),
            nameAndFirstnameTextView
        ).toString()

        session.showPhotoUser(this, photoImageView)

        //Actualise badges when changes and messages
        FirebaseDatabase.getInstance().getReference("discussions").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //Nothing
            }

            override fun onDataChange(p0: DataSnapshot) {
                //menu.setBadges()
            }

        })
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.infos -> {
                val checkAccountIntent = Intent(this, UserInfoActivity::class.java)
                startActivity(checkAccountIntent)
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
            R.id.deconnexion -> {
                val logOutAlert = AlertDialog.Builder(this)
                logOutAlert.setTitle("Log out?")
                logOutAlert.setPositiveButton("Yes"){ _, _ ->
                    session.signOut()
                    googleSignInClient.signOut()
                    LoginManager.getInstance().logOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    finish()
                    startActivity(intent)
                }
                logOutAlert.setNegativeButton("No"){ _, _ ->

                }
                logOutAlert.show()


            }
            R.id.friends_layout -> {
                startActivity(Intent(this, FriendsActivity::class.java))
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
            R.id.events_layout -> {
                startActivity(Intent(this, EventActivity::class.java))
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
            R.id.notificationPush -> {
                startActivity(Intent(this, NotificationPushActivity::class.java))
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out)
            }
            R.id.share_application -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                intent.putExtra(Intent.EXTRA_TEXT, "Share this application with your friends at : https://play.google.com/store/apps/details?id=com.google.android.apps.plus")
                startActivity(Intent.createChooser(intent, "choose one"))
            }
        }
    }

    fun loadFragment(fragment: Fragment){
        if(!isFinishing) {
            supportFragmentManager.beginTransaction().replace(R.id.HomeFragment, fragment).commit()
        }
    }

}
