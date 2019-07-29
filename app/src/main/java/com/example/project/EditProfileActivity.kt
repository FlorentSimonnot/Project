package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.picker.StringPickerCustom
import com.example.place.SessionGooglePlace
import com.example.session.SessionUser
import com.example.user.Gender
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

class EditProfileActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener  {
    var session : SessionUser = SessionUser()
    val stringSex = arrayOf("Male", "Female", "Other")
    private lateinit var sexTextView: TextView
    private var placeId : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val nameEditText = findViewById<EditText>(R.id.name_account)
        val firstNameEditText = findViewById<EditText>(R.id.firstName_account)
        val emailEditText = findViewById<EditText>(R.id.email_account)
        /*val sexSpinner = findViewById<Spinner>(R.id.sex_spinner)*/
        sexTextView = findViewById<TextView>(R.id.edit_sexe)
        val birthdayEditText = findViewById<EditText>(R.id.birthday_account)
        /*val cityEditText = findViewById<EditText>(R.id.city_account)*/
        val descriptionEditText = findViewById<EditText>(R.id.describe_account)
        val modifyPasswordButton = findViewById<Button>(R.id.modify_password_button)
        val confirmChangesButton = findViewById<Button>(R.id.confirm_changes)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nameEditText.hint = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            nameEditText,
            "name"
        ).toString()

        firstNameEditText.hint = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            firstNameEditText,
            "firstName"
        ).toString()


        emailEditText.hint = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            emailEditText,
            "email"
        ).toString()

        sexTextView.text = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            sexTextView,
            "sex"
        ).toString()

        sexTextView.setOnClickListener {
            val numberPicker = StringPickerCustom(0, 2, "Gender", "", stringSex)
            numberPicker.setValueChangeListener(this)
            numberPicker.show(supportFragmentManager, "Sex picker")
        }

        birthdayEditText.hint = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            birthdayEditText,
            "birthday"
        ).toString()


        /*cityEditText.hint = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            cityEditText,
            "city"
        ).toString()*/

        val gg = SessionGooglePlace(applicationContext)
        gg.init()
/*
        val placesClient = gg.createClient()
*/

        //Autocomplete city
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?

        // Specify the types of place data to return.
        autocompleteFragment!!
            .setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME))

        autocompleteFragment
            .setTypeFilter(TypeFilter.CITIES)


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                placeId = ""
            }

            override fun onPlaceSelected(place: Place) {
                placeId = place.id
            }

        })

        descriptionEditText.hint = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            descriptionEditText,
            "describe"
        ).toString()

        modifyPasswordButton.setOnClickListener {
            startActivity(Intent(this, ModifyPasswordActivity::class.java))
        }

        confirmChangesButton.setOnClickListener {
            val sex = when (sexTextView.text) {
                "Male" -> Gender.Male
                "Female" -> Gender.Female
                else -> Gender.Other
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm changes")
            builder.setMessage("Your profile will be updated.\nConfirm?")
            builder.setPositiveButton("Yes"){_, _ ->
                session.updateAccount(
                    nameEditText.text.toString(),
                    firstNameEditText.text.toString(),
                    emailEditText.text.toString(),
                    sex,
                    birthdayEditText.text.toString(),
                    placeId.toString(),
                    descriptionEditText.text.toString()
                )
                startActivity(Intent(this, UserInfoActivity::class.java))
            }
            builder.setNegativeButton("No"){_, _ ->

            }
            builder.show()
        }

    }

    override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
        if(p0 != null){
            if(p0.maxValue == 2) {
                sexTextView.text = stringSex[p0.value]
            }
        }
    }

}
