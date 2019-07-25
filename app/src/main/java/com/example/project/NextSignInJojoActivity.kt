package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.form.FormSignInSecond
import com.example.place.SessionGooglePlace
import com.example.session.SessionUser
import com.example.user.Gender
import com.example.user.User
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.LocalDate.*
import java.util.*

class NextSignInJojoActivity : AppCompatActivity() {
    private var name : String = ""
    private var firstName : String = ""
    private var password : String = ""
    private var email : String = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var typeLog : String
    private var placeId : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next_sign_in_jojo)

        auth = FirebaseAuth.getInstance()
        //Init google place
        val gg = SessionGooglePlace(applicationContext)
        gg.init()
        val placesClient = gg.createClient()

        //Autocomplete city
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment?.setHint("Choose your city")


        // Specify the types of place data to return.
        autocompleteFragment!!
            .setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME))

        autocompleteFragment
            .setTypeFilter(TypeFilter.CITIES)


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                println("AN ERROR OCCURED $p0")
                placeId = ""
            }

            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                placeId = place.id
            }

        })

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
            val sex : Gender = when(findViewById<Spinner>(R.id.sexe_spinner).selectedItem.toString()){
                "Male" -> Gender.MALE
                "Female" -> Gender.FEMALE
                else -> Gender.ALIEN
            }
            val form = FormSignInSecond(
                sex,
                findViewById<EditText>(R.id.birthday).text.toString(),
                findViewById<EditText>(R.id.description).text.toString(),
                placeId.toString()
            )

            if(form.isFormValid()){
                val user = User(
                    firstName,
                    name,
                    email,
                    password,
                    sex,
                    findViewById<EditText>(R.id.birthday).text.toString(),
                    findViewById<EditText>(R.id.description).text.toString(),
                    placeId.toString(),
                    typeLog,
                    idServiceLogin
                )

                when(typeLog){
                    "Email" -> {
                        user.createAccount(auth, this)
                    }
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
