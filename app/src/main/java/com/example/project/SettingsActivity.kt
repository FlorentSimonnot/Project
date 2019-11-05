package com.example.project

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.dialog.AlertCustomWithRadioGroup
import com.example.session.SessionUser
import com.example.session.SharedPref
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var darkThemeLayout : RelativeLayout
    private lateinit var darkThemeValue : TextView
    private lateinit var measuringSystemLayout : RelativeLayout
    private lateinit var measuringSystemValue : TextView
    private lateinit var sportsSettingsLayout : RelativeLayout
    private lateinit var sportsSettingsValue : TextView
    private val session : SessionUser = SessionUser(this)
    private var valuesMeasuringSystem : Array<String> = Array(2, {""})
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        darkThemeLayout = findViewById(R.id.layoutDarkTheme)
        darkThemeValue = findViewById(R.id.nightModeValue)

        measuringSystemLayout = findViewById(R.id.layoutDistance)
        measuringSystemValue = findViewById(R.id.measuringSystemValue)

        sportsSettingsLayout = findViewById(R.id.layoutSports)
        sportsSettingsValue = findViewById(R.id.sportsValue)

        darkThemeLayout.setOnClickListener(this)
        measuringSystemLayout.setOnClickListener(this)
        sportsSettingsLayout.setOnClickListener(this)

        valuesMeasuringSystem[0] = "Km"
        valuesMeasuringSystem[1] = "Miles"

        initDarkThemeValue()
        initMeasuringSystem()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.layoutDarkTheme -> {
                session.setNightMode()
                finish()
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.layoutDistance -> {
                arrayAdapter = ArrayAdapter(this, R.layout.list_radio, valuesMeasuringSystem)
                val a = AlertCustomWithRadioGroup(this, this, arrayAdapter, "Measuring system")
                createAlertDialogMeasuringSystem(a)
            }
            R.id.layoutSports -> {
                startActivity(Intent(this, SettingsSport::class.java))
            }
        }
    }

    private fun initDarkThemeValue(){
        if(SharedPref(this@SettingsActivity).loadNightModeState()!!){
            darkThemeValue.text = this@SettingsActivity.getText(R.string.dark_theme_activate)
        }else{
            darkThemeValue.text = this@SettingsActivity.getText(R.string.dark_theme_deactivate)
        }
        /*FirebaseDatabase.getInstance().getReference("parameters/${SessionUser(this).getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("nightMode")){
                    if(p0.child("nightMode").value as Boolean){
                        darkThemeValue.text = this@SettingsActivity.getText(R.string.dark_theme_activate)
                    }
                    else{
                        darkThemeValue.text = this@SettingsActivity.getText(R.string.dark_theme_deactivate)
                    }
                }
                else{
                    darkThemeValue.text = this@SettingsActivity.getText(R.string.dark_theme_deactivate)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })*/
    }

    private fun initMeasuringSystem(){
        FirebaseDatabase.getInstance().getReference("parameters/${SessionUser(this).getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("measuringSystem")){
                    if(p0.child("measuringSystem").value as String == "Km"){
                        measuringSystemValue.text = this@SettingsActivity.getText(R.string.measuring_system_sub_km)
                    }
                    else{
                        measuringSystemValue.text = this@SettingsActivity.getText(R.string.measuring_system_sub_milles)
                    }
                }
                else{
                    measuringSystemValue.text = this@SettingsActivity.getText(R.string.measuring_system_sub_km)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun createAlertDialogMeasuringSystem(alertCustomWithRadioGroup: AlertCustomWithRadioGroup){
        FirebaseDatabase.getInstance().getReference("parameters/${SessionUser(this).getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("measuringSystem")){
                    if(p0.child("measuringSystem").value as String == "Km"){
                        alertCustomWithRadioGroup.create("Km")
                    }
                    else{
                        alertCustomWithRadioGroup.create("Miles")
                    }
                }
                else{
                    alertCustomWithRadioGroup.create("Km")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

}