package com.example.project

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.dialog.AlertCustomWithRadioGroup
import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var nightMode : TextView
    private var valuesNightMode : Array<String> = Array(2, {""})
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nightMode = findViewById(R.id.nightMode)
        nightMode.setOnClickListener(this)

        valuesNightMode[0] = "Activate"
        valuesNightMode[1] = "Deactivate"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.nightMode -> {
                arrayAdapter = ArrayAdapter(this, R.layout.list_radio, valuesNightMode)
                val a = AlertCustomWithRadioGroup(this, this, arrayAdapter, "Night mode")
                createAlertDialogNightMode(a)
            }
        }
    }

    private fun createAlertDialogNightMode(alertCustomWithRadioGroup: AlertCustomWithRadioGroup){
        FirebaseDatabase.getInstance().getReference("parameters/${SessionUser(this).getIdFromUser()}").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("nightMode")){
                    if(p0.child("nightMode").value as Boolean){
                        alertCustomWithRadioGroup.create(true)
                    }
                    else{
                        alertCustomWithRadioGroup.create(false)
                    }
                }
                else{
                    alertCustomWithRadioGroup.create(false)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

}