package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.form.FormSignInSecond
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
                user.createAccount(auth)
            }
        }


        /*val sexe_array = arrayOf("Male", "Female", "Alien")
        val spinner = findViewById<Spinner>(R.id.sexe_spinner)
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            sexe_array
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }*/
    }

    /*private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // [START_EXCLUDE]
                hideProgressDialog()
                // [END_EXCLUDE]
            }
        // [END create_user_with_email]
    }*/
}
