package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.session.SessionUser

class ModifyPasswordActivity : AppCompatActivity() {
    var session = SessionUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_password)

        val currentPasswordEditText = findViewById<EditText>(R.id.current_password_editText)
        val newPasswordEditText = findViewById<EditText>(R.id.new_password_editText)
        val confirNewPasswordEditText = findViewById<EditText>(R.id.confirm_new_password_editText)
        val confirmPasswordChangesButton = findViewById<Button>(R.id.confirm_password_changes_button)
        val check = TextView(this)

        confirmPasswordChangesButton.setOnClickListener {
            if (session.checkPassword(currentPasswordEditText.text.toString(), check)) {
                if (newPasswordEditText.text.toString() == confirNewPasswordEditText.text.toString()) {
                    session.setNewPassword(session.getIdFromUser(), confirNewPasswordEditText.text.toString())
                }
            }
        }

    }
}
