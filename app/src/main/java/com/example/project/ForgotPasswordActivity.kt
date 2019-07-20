package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.form.FormResetPassword
import com.example.session.SessionUser
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private val session = SessionUser()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        val buttonResetPassword : Button = findViewById(R.id.btn_send_email_reset_password)
        val email : EditText = findViewById(R.id.email)

        setSupportActionBar(toolbar)
        toolbar.title = "Reset your password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonResetPassword.setOnClickListener {
            val formResetPassword = FormResetPassword(email.text.toString())
            if(formResetPassword.isFormValid()) {
                session.resetPassword(this, auth, email.text.toString())
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
