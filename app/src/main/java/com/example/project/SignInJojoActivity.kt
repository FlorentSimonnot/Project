package com.example.project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.form.FormSignInFirst
import com.example.validator.validateInputPassword

class SignInJojoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_jojo)

        val nextButton = findViewById<Button>(R.id.next)

        nextButton.setOnClickListener {
            var form = FormSignInFirst(
                findViewById<EditText>(R.id.first_name).text.toString(),
                findViewById<EditText>(R.id.name).text.toString(),
                findViewById<EditText>(R.id.email_address).text.toString(),
                findViewById<EditText>(R.id.password).text.toString(),
                findViewById<EditText>(R.id.confirm_password).text.toString()
                )


            if(form.isFormValid()){
                val nextSignInJojo = Intent(this, NextSignInJojoActivity::class.java)
                nextSignInJojo.action = Context.INPUT_SERVICE
                nextSignInJojo.addCategory("UserSignIn")
                nextSignInJojo.putExtra("firstName", form.firstName)
                nextSignInJojo.putExtra("name", form.name)
                nextSignInJojo.putExtra("email", form.email)
                nextSignInJojo.putExtra("password", form.password)
                startActivity(nextSignInJojo)
            }
            else{
                val errorInput : TextView = findViewById<EditText>(R.id.errorMessage)
                errorInput.text = "An error occurred with password."
            }
        }
    }
}
