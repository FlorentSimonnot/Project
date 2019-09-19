package com.example.Tutorial

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.project.HomeActivity
import com.example.project.R
import com.example.session.SessionUser
import com.google.firebase.database.FirebaseDatabase

class PageFragment3 : Fragment(), View.OnClickListener {
    private lateinit var buttonNext : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.page3, null)
        buttonNext = v.findViewById(R.id.buttonTutoSee)
        buttonNext.setOnClickListener(this)


        return v
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.buttonTutoSee -> {
                FirebaseDatabase.getInstance().reference.child("parameters/${SessionUser(context!!).getIdFromUser()}/tutorialBeginner").setValue(true)
                val intent = Intent(context!!, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context!!.startActivity(intent)
            }
        }
    }

}