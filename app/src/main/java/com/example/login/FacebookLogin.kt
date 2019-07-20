package com.example.login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

class FacebookLogin (private val buttonFacebookLogin: LoginButton){
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private var TAG : String = "LOGIN FACEBOOK"

    fun login() : Boolean{
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        var res = false

        //buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                res = handleFacebookAccessToken(loginResult.accessToken)
                println("SUCCCESSS ? $res")
            }

            override fun onCancel() {
                println(" ERROR CANCEL :::::: ")
            }

            override fun onError(error: FacebookException) {
                println(" ERROR :::::: $error")
            }
        })
        return res
    }

    private fun handleFacebookAccessToken(token: AccessToken) : Boolean{
        Log.d(TAG, "handleFacebookAccessToken:$token")
        var res : Boolean = false
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() {
                if (it.isSuccessful) {
                    val user = auth.currentUser
                    res = true
                    println("YESSS ")
                } else {
                    println("ERRROR 2 ::: ${it.exception}")
                }
            }
        return res
    }
}