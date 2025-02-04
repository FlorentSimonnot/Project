package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.session.SessionUser
import kotlinx.android.synthetic.main.activity_user_info.*

class UserInfoActivity : AppCompatActivity(), View.OnClickListener {

    var session = SessionUser(this)
    private lateinit var progressBar: ProgressBar
    private lateinit var radiusSeekBar : ProgressBar
    private lateinit var radiusSeekBarTint : RelativeLayout
    private lateinit var radiusTextView : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.my_personnal_information)
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
        radiusSeekBar = findViewById(R.id.radius)
        radiusTextView = findViewById(R.id.seekbar_value)
        progressBar = findViewById(R.id.progressBar)

        progressBar.visibility = View.VISIBLE

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

        progressBar.visibility = View.GONE

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        updateInfos()
    }

    private fun updateInfos() {
        session.writeRadius(this, session.getIdFromUser(), radiusSeekBar, radiusTextView)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_profile_button -> {
                startActivity(Intent(this, EditProfileActivity::class.java))
            }
            R.id.btn_delete_account -> {

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.user_info_delete_dialog_title))
                builder.setMessage(getString(R.string.user_info_delete_dialog_message))
                builder.setPositiveButton(getString(R.string.user_info_delete_dialog_yes)){_, _ ->
                    session.deleteUser(this)
                }
                builder.setNegativeButton(getString(R.string.user_info_delete_dialog_no)){_, _ ->

                }
                builder.show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
