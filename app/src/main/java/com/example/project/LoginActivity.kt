package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.login.FacebookLogin
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import com.example.session.SessionUser


class LoginActivity : AppCompatActivity() {
    private val sessionUser = SessionUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonFacebookLogin : LoginButton = this.findViewById(R.id.facebookLoginButton)
        val buttonLogin : Button = this.findViewById(R.id.buttonLogin)
        val notAMember : Button = findViewById(R.id.notAMember)

        buttonFacebookLogin.setOnClickListener{
            val facebookLogin = FacebookLogin(buttonFacebookLogin)
            facebookLogin.login()
        }
        val mAuth = FirebaseAuth.getInstance()

        buttonLogin.setOnClickListener {
            val email : String = findViewById<EditText>(R.id.email).text.toString()
            val password : String = findViewById<EditText>(R.id.password).text.toString()
            sessionUser.login(email, password, this)
        }

        notAMember.setOnClickListener {
            val intent = Intent(this, SignInJojoActivity::class.java)
            startActivity(intent)
        }

    }
}
