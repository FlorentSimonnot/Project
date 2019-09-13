package com.example.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
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



class EditProfileActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener  {
    var session : SessionUser = SessionUser(this)
    val stringSex = arrayOf("Male", "Female", "Other")
    private lateinit var sexTextView: TextView
    private var placeId : String? = ""
    private lateinit var cityEditText: TextView
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private val photoRequestCode = 1818
    private lateinit var photo : ImageView
    private var uri : Uri? = null
    private lateinit var radiusTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit profil"

        val emailEditText = findViewById<EditText>(R.id.email_account)
        sexTextView = findViewById(R.id.edit_sexe)
        val birthdayEditText = findViewById<EditText>(R.id.birthday_account)
        cityEditText = findViewById(R.id.city_account)
        val descriptionEditText = findViewById<EditText>(R.id.describe_account)
        val modifyPasswordButton = findViewById<Button>(R.id.modify_password_button)
        val confirmChangesButton = findViewById<ImageButton>(R.id.confirm_changes)
        photo = findViewById(R.id.profile_photo)
        val buttonImage = findViewById<Button>(R.id.change_photo)

        val radiusSeekBar = findViewById<SeekBar>(R.id.radius)
        radiusTextView = findViewById(R.id.seekbar_value)

        session.showPhotoUser(this, photo)

        cityEditText.setOnClickListener(this)

        buttonImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, photoRequestCode)
        }


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

        session.writeRadius(this, session.getIdFromUser(), radiusSeekBar, radiusTextView)

        radiusSeekBar.setOnSeekBarChangeListener(this)

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
                    descriptionEditText.text.toString(),
                    uri,
                    radiusSeekBar.progress
                )
                startActivity(Intent(this, HomeActivity::class.java))
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
        return true
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data != null) {
            when(requestCode){
                AUTOCOMPLETE_REQUEST_CODE -> {
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
                photoRequestCode -> {
                    if(resultCode == Activity.RESULT_OK){
                        uri = data.data!!
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        val bitmapDrawable = BitmapDrawable(bitmap)
                        photo.setImageDrawable(bitmapDrawable)
                    }
                }
            }
        }
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if(p0 != null){
            radiusTextView.text = "$p1 km"
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}

    override fun onStopTrackingTouch(p0: SeekBar?) {}


}
