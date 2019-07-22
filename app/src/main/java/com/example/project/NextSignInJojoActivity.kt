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
    private lateinit var typeLog : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next_sign_in_jojo)

        auth = FirebaseAuth.getInstance()

        val action = intent.action
        val infos : Bundle? = intent.extras
        if(intent.hasCategory("UserSignInWithEmail")){
            typeLog = "Email"
        }
        else if(intent.hasCategory("UserSignInWithFacebook")){
            typeLog = "Facebook"
        }
        else{
            typeLog = "Google"
        }
        firstName = infos?.getString("firstName").toString()
        name = infos?.getString("name").toString()
        password = infos?.getString("password").toString()
        email = infos?.getString("email").toString()
        var idServiceLogin = infos?.getString("id").toString()

        val textViewInfos : TextView = findViewById(R.id.textInfos)
        textViewInfos.text = "Hi $firstName please complete your profil before sign in !"

        val seekBar : SeekBar = findViewById(R.id.distance_seekbar)

        val textViewSeekBar = findViewById<TextView>(R.id.distance_value)

        textViewSeekBar.text = "${seekBar.progress.toString()}km"
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    textViewSeekBar.text = "${seekBar.progress.toString()}km"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            }
        )

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
                    findViewById<EditText>(R.id.city).text.toString(),
                    typeLog,
                    idServiceLogin
                )

                when(typeLog){
                    "Email" -> user.createAccount(auth, this)
                    "Google" -> {
                        user.insertUser(auth.currentUser?.uid)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    "Facebook" -> {
                        user.insertUser(auth.currentUser?.uid)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }


            }
        }


    }

    override fun onBackPressed() {
        auth.currentUser?.delete() //Delete the user because we quit the activity
        finish()
    }

}
