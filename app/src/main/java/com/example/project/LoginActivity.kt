package com.example.project

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import com.example.login.FacebookLogin
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import com.example.login.GoogleLogin
import com.example.session.SessionUser
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import java.security.MessageDigest
import com.google.android.gms.common.util.IOUtils.toByteArray
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {
    private val sessionUser = SessionUser()
    private val RC_SIGN_IN = 9001
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val mAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        //Config Google sign in
        val googleSigninOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSigninOptions)


        val buttonFacebookLogin : Button = this.findViewById(R.id.facebookLoginButton)
        val buttonGoogleLogin : Button = this.findViewById(R.id.googleLoginButton)
        val passwordForgot : Button = findViewById(R.id.forgotPassword)
        val buttonLogin : Button = this.findViewById(R.id.buttonLogin)
        val notAMember : Button = findViewById(R.id.notAMember)

        buttonFacebookLogin.setOnClickListener{
            val facebookLogin = FacebookLogin(this, buttonFacebookLogin, callbackManager)
            facebookLogin.login(this)
        }

        buttonLogin.setOnClickListener {
            val email : String = findViewById<EditText>(R.id.email).text.toString()
            val password : String = findViewById<EditText>(R.id.password).text.toString()
            sessionUser.login(email, password, this)
        }

        buttonGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        notAMember.setOnClickListener {
            val intent = Intent(this, SignInJojoActivity::class.java)
            startActivity(intent)
        }

        passwordForgot.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Google
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                var googleLogin = GoogleLogin(googleSignInClient, account!!)
                googleLogin.login(this)
            } catch (e: ApiException) {
                println("Google sign in failed : ${e.statusCode}")
            }
        }
        //Facebook
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

}
