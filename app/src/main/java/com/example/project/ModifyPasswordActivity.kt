package com.example.project

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.session.SessionUser

class ModifyPasswordActivity : AppCompatActivity() {
    var session = SessionUser(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_password)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val currentPasswordEditText = findViewById<EditText>(R.id.current_password_editText)
        val newPasswordEditText = findViewById<EditText>(R.id.new_password_editText)
        val confirNewPasswordEditText = findViewById<EditText>(R.id.confirm_new_password_editText)
        val confirmPasswordChangesButton = findViewById<Button>(R.id.confirm_password_changes_button)
        val textView = TextView(this)

        textView.text = session.writeInfoUser(
            this,
            session.getIdFromUser(),
            textView,
            "password"
        ).toString()
        confirmPasswordChangesButton.setOnClickListener {
            if (currentPasswordEditText.text.toString() == textView.text.toString()) {

                if (newPasswordEditText.text.toString() == confirNewPasswordEditText.text.toString()) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Change your password")
                    builder.setMessage("Do you really want to change your password?")
                    builder.setPositiveButton("Confirm"){_, _ ->
                        session.setNewPassword(currentPasswordEditText.text.toString(), confirNewPasswordEditText.text.toString())
                        startActivity(Intent(this, EditProfileActivity::class.java))
                    }
                    builder.setNegativeButton("No"){_, _ -> }
                    builder.show()

                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        EventInfoJojoActivity::finish
        startActivity(intent)
        return true
    }
}
