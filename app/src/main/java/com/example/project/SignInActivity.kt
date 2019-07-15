package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



class SignInActivity : AppCompatActivity() {
    private var username : String? = ""
    private var email : String? = ""
    private var password : String? = ""
    private var confirmPassword : String? = ""

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        val buttonSignIn : Button = findViewById(R.id.buttonSignIn)

        buttonSignIn.setOnClickListener{createAccount(it)}

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        mAuth!!
            .createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val userId = mAuth!!.currentUser!!.uid
                    //Verify Email
                    verifyEmail();
                    //update user profile information
                    val currentUserDb = mDatabaseReference!!.child(userId)
                    //currentUserDb.child("firstName").setValue(firstName)
                    //currentUserDb.child("lastName").setValue(lastName)
                    //updateUserInfoAndUI()
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    //Toast.makeText(this@CreateAccountActivity, "Authentication failed.",
                        //Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createAccount(view : View) {
        username  = view.findViewById<EditText>(R.id.username).text.toString()
        email  = view.findViewById<EditText>(R.id.email).text.toString()
        password  = findViewById<EditText>(R.id.password).text.toString()
        confirmPassword  = findViewById<EditText>(R.id.confirmPassword).text.toString()
        //Log.d("email", email!!)
    }

    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser;
        val addOnCompleteListener = mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("rez", "verify mail ok")
                }
                /*Toast.makeText(this@CreateAccountActivity,
                    "Verification email sent to " + mUser.getEmail(),
                    Toast.LENGTH_SHORT).show()*/
                else {
                    Log.d("res", "sendEmailVerification")
                }
            }
    }

    /*public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.getCurrentUser()
        //updateUI(curent)
    }*/



}
