package com.example.project

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.session.SessionUser
import com.example.user.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_next_sign_in_jojo.*
import kotlinx.android.synthetic.main.activity_user_info.*
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.widget.ImageView


class UserInfoActivity : AppCompatActivity() {
    private lateinit var textMessage: TextView
    var session : SessionUser = SessionUser()

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val nameTextView = findViewById<TextView>(R.id.name_account)
        val firstNameTextView = findViewById<TextView>(R.id.firstName_account)
        val emailTextView = findViewById<TextView>(R.id.email_account)
        val sexTextView = findViewById<TextView>(R.id.sex_account)
        val birthdayTextView = findViewById<TextView>(R.id.birthday_account)
        val cityTextView = findViewById<TextView>(R.id.city_account)
        val describeTextView = findViewById<TextView>(R.id.describe_account)

        val signOut = findViewById<ImageButton>(R.id.btn_logout)
        val delete = findViewById<Button>(R.id.btn_delete_account)
        signOut.setOnClickListener {
            session.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        delete.setOnClickListener {
            session.deleteUser(this)
        }

        println("UIDDD : ${session.getIdFromUser()}")

        nameTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            nameTextView,
            "name",
            ""
        ).toString()

        firstNameTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            firstNameTextView,
            "firstName",
            ""
        ).toString()


        emailTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            emailTextView,
            "email",
            ""
        ).toString()


        sexTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            sexTextView,
            "sex",
            ""
        ).toString()


        birthdayTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            birthdayTextView,
            "birthday",
            ""
        ).toString()


        cityTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            cityTextView,
            "city",
            ""
        ).toString()


        describeTextView.text = session.writeInfoUser(
            session.getIdFromUser(),
            describeTextView,
            "describe",
            ""
        ).toString()

    }

}
