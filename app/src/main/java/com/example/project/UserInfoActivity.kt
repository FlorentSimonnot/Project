package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.session.SessionUser

class UserInfoActivity : AppCompatActivity(), View.OnClickListener {

    var session = SessionUser(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "My personal information"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /* BUTTON ACTIONS */
        findViewById<Button>(R.id.btn_delete_account).setOnClickListener(this)
        findViewById<Button>(R.id.edit_profile_button).setOnClickListener(this)

        val emailTextView = findViewById<TextView>(R.id.email_account)
        val sexTextView = findViewById<TextView>(R.id.sex_account)
        val birthdayTextView = findViewById<TextView>(R.id.birthday_account)
        val cityTextView = findViewById<TextView>(R.id.city_account)
        val describeTextView = findViewById<TextView>(R.id.describe_account)
        val photoImageView = findViewById<ImageView>(R.id.profile_photo)

        session.showPhotoUser(this, photoImageView)


        emailTextView.text = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            emailTextView,
            "email"
        ).toString()


        sexTextView.text = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            sexTextView,
            "sex"
        ).toString()


        birthdayTextView.text = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            birthdayTextView,
            "birthday"
        ).toString()


        cityTextView.text = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            cityTextView,
            "city"
        ).toString()


        describeTextView.text = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            describeTextView,
            "describe"
        ).toString()

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_profile_button -> {
                startActivity(Intent(this, EditProfileActivity::class.java))
            }
            R.id.btn_delete_account -> {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete user account")
                builder.setMessage("Do you really want to delete your account?")
                builder.setPositiveButton("Confirm"){_, _ ->
                    session.deleteUser(this)
                }
                builder.setNegativeButton("No"){_, _ ->

                }
                builder.show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val intent = Intent(this, ActivityInfoUser::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        EventInfoJojoActivity::finish
        startActivity(intent)
        return true
    }
}
