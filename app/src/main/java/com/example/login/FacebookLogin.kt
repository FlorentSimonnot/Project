package com.example.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.project.MainActivity
import com.example.project.NextSignInJojoActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.facebook.GraphResponse
import org.json.JSONObject
import com.facebook.GraphRequest



class FacebookLogin (private val buttonFacebookLogin: LoginButton,
     private var callbackManager: CallbackManager
 ){
    private lateinit var auth: FirebaseAuth
    private lateinit var context : Context

    fun login(context: Context) : Boolean{
        auth = FirebaseAuth.getInstance()

        this.context = context

        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                println("facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                println(" ERROR CANCEL :::::: ")
            }

            override fun onError(error: FacebookException) {
                println(" ERROR :::::: $error")
            }
        })
        return true
    }

    private fun handleFacebookAccessToken(token: AccessToken) : Boolean{
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() {
                if (it.isSuccessful) {
                    val user = auth.currentUser
                    if(it.result != null) {
                        if (it.result!!.additionalUserInfo.isNewUser) {
                            //Get data about facebook user
                            val request = GraphRequest.newMeRequest(token) { `object`, response ->
                                try {
                                    //here is the data that you want
                                    val nextSignInJojo = Intent(context, NextSignInJojoActivity::class.java)
                                    nextSignInJojo.action = Context.INPUT_SERVICE
                                    nextSignInJojo.addCategory("UserSignInWithFacebook")
                                    nextSignInJojo.putExtra("firstName", `object`.getString("first_name"))
                                    nextSignInJojo.putExtra("name", `object`.getString("last_name"))
                                    nextSignInJojo.putExtra("email", `object`.getString("email"))
                                    nextSignInJojo.putExtra("password", "")
                                    nextSignInJojo.putExtra("uid", user?.uid!!)
                                    nextSignInJojo.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(nextSignInJojo)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            val parameters = Bundle()
                            parameters.putString("fields", "first_name, last_name, email,id,picture.type(large)")
                            request.parameters = parameters
                            request.executeAsync()
                        } else {
                            context.startActivity(Intent(context, MainActivity::class.java))
                        }
                    }
                } else {
                    println("ERRROR ::: ${it.exception}")
                }
            }
        return true
    }
}