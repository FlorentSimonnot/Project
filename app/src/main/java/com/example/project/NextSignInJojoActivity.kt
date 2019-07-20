package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.form.FormSignInSecond
import com.example.session.SessionUser
import com.example.user.Gender
import com.example.user.User
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.LocalDate.*

class NextSignInJojoActivity : AppCompatActivity() {
    private var name : String = ""
    private var firstName : String = ""
    private var password : String = ""
    private var email : String = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next_sign_in_jojo)

        auth = FirebaseAuth.getInstance()

        val action = intent.action
        val isUserSignIn = intent.hasCategory("UserSignIn")
        var infos : Bundle? = intent.extras
        firstName = infos?.getString("firstName").toString()
        name = infos?.getString("name").toString()
        password = infos?.getString("password").toString()
        email = infos?.getString("email").toString()

        val textViewInfos : TextView = findViewById(R.id.textInfos)
        textViewInfos.text = "Hi $firstName please complete your profil before sign in !"

        val buttonJoinUs : Button = findViewById(R.id.create_account)
        buttonJoinUs.setOnClickListener{
            var sex : Gender = when(findViewById<Spinner>(R.id.sexe_spinner).selectedItem.toString()){
                "Male" -> Gender.MALE
                "Female" -> Gender.FEMALE
                else -> Gender.ALIEN
            }
            var form = FormSignInSecond(
                sex,
                findViewById<EditText>(R.id.birthday).text.toString(),
                findViewById<EditText>(R.id.description).text.toString(),
                findViewById<EditText>(R.id.city).text.toString()
            )

            if(form.isFormValid()){
                var user = User(
                    firstName,
                    name,
                    email,
                    password,
                    sex,
                    findViewById<EditText>(R.id.birthday).text.toString(),
                    findViewById<EditText>(R.id.description).text.toString(),
                    findViewById<EditText>(R.id.city).text.toString()
                )

                if(user.createAccount(auth, this)){
                    println("LETS GO !!")
                }
                else{
                    println("   BEURK !!!! ")
                }

            }
        }


    }

}
