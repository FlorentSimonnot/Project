package com.example.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.picker.StringPickerCustom
import com.example.place.SessionGooglePlace
import com.example.session.SessionUser
import com.example.user.Gender
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_user_info.*
import java.util.*
import com.google.android.libraries.places.widget.AutocompleteActivity



class EditProfileActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener, View.OnClickListener  {

    var session : SessionUser = SessionUser()
    val stringSex = arrayOf("Male", "Female", "Other")
    private lateinit var sexTextView: TextView
    private var placeId : String? = ""
    private lateinit var cityEditText: TextView
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        var toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val emailEditText = findViewById<EditText>(R.id.email_account)
        /*val sexSpinner = findViewById<Spinner>(R.id.sex_spinner)*/
        sexTextView = findViewById<TextView>(R.id.edit_sexe)
        val birthdayEditText = findViewById<EditText>(R.id.birthday_account)
        cityEditText = findViewById(R.id.city_account)
        val descriptionEditText = findViewById<EditText>(R.id.describe_account)
        val modifyPasswordButton = findViewById<Button>(R.id.modify_password_button)
        val confirmChangesButton = findViewById<Button>(R.id.confirm_changes)

        cityEditText.setOnClickListener(this)


        /*nameEditText.hint = session.writeInfoUser(
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
        ).toString()*/


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


        cityEditText.hint = session.writeInfoUser(
            applicationContext,
            session.getIdFromUser(),
            cityEditText,
            "city"
        ).toString()

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
                    emailEditText.text.toString(),
                    sex,
                    birthdayEditText.text.toString(),
                    placeId.toString(),
                    descriptionEditText.text.toString()
                )
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Your account has been successfully updated!", Toast.LENGTH_LONG).show()
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

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.city_account -> {
                val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME)
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .setTypeFilter(TypeFilter.CITIES)
                    .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val intent = Intent(this, UserInfoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        EventInfoJojoActivity::finish
        startActivity(intent)
        return true
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data != null) {
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                when (resultCode) {
                    AUTOCOMPLETE_REQUEST_CODE -> {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        placeId = place.id
                        cityEditText.text = place.name
                    }
                    AutocompleteActivity.RESULT_ERROR -> {
                        val status = Autocomplete.getStatusFromIntent(data)
                    }
                    Activity.RESULT_CANCELED -> {
                    }
                }
            }
        }
    }

}
