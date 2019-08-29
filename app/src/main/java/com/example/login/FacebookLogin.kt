package com.example.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.project.LoginActivity
import com.example.project.MainActivity
import com.example.project.NextSignInJojoActivity
import com.example.session.SessionUser
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
import com.facebook.login.LoginManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*


class FacebookLogin (
    private val activity: Activity,
    private val buttonFacebookLogin: Button,
     private var callbackManager: CallbackManager
 ){
    private lateinit var auth: FirebaseAuth

    fun login(context: Context) : Boolean{
        auth = FirebaseAuth.getInstance()

        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"))

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

            }

            override fun onSuccess(loginResult: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener() {
                        if (it.isSuccessful) {
                            val user = auth.currentUser
                            if (it.result != null) {
                                if (it.result!!.additionalUserInfo.isNewUser) {
                                    //Get data about facebook user
                                    val request =
                                        GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
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
                                                nextSignInJojo.putExtra("id", `object`.getString("id"))
                                                nextSignInJojo.flags =
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(nextSignInJojo)

                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    val parameters = Bundle()
                                    parameters.putString(
                                        "fields",
                                        "first_name, last_name, email, id, picture.type(large)"
                                    )
                                    request.parameters = parameters
                                    request.executeAsync()
                                } else {
                                    FirebaseInstanceId.getInstance().instanceId
                                        .addOnCompleteListener {
                                            if(!it.isSuccessful){
                                                println("ERRRORRRR")
                                            }
                                            val session = SessionUser(context)
                                            val token = it.result?.token
                                            val ref = FirebaseDatabase.getInstance().getReference("users")
                                            ref.child("${session.getIdFromUser()}").child("idTokenRegistration").setValue(token)
                                        }
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                }
                            }
                        } else {
                            println("ERRROR ::: ${it.exception}")
                            Toast.makeText(context, "Can't login ! ${it.exception}", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        })

        return true
    }


}