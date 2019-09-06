package com.example.arrayAdapterCustom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.events.Event
import com.example.project.R
import com.example.session.SessionUser
import com.example.sport.Sport
import com.example.sport.SportWithBoolean

class ArrayAdapterSportWithBoolean(private val ctx : Context, private val resource : Int, private val sports : ArrayList<SportWithBoolean>)
    : ArrayAdapter<SportWithBoolean>(ctx , resource, sports){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)


        val view : View = layoutInflater.inflate(resource , null )
        val imageView : ImageView = view.findViewById(R.id.icon_sport)
        val textView : TextView = view.findViewById(R.id.name_sport)
        val btn : Switch = view.findViewById(R.id.switch_sport)

        imageView.setImageDrawable(ctx.resources.getDrawable(sports[position].sport.getLogoSport()))
        textView.text = sports[position].sport.getNameSport()
        if(sports[position].boolean){
            btn.isChecked = true
            //btn.setImageDrawable(context.resources.getDrawable(R.drawable.ic_checked_color_primary))

        }
        else{
            btn.isChecked = false
            //btn.setImageDrawable(context.resources.getDrawable(R.drawable.ic_cross))
        }

        view.setOnClickListener {
            sports[position].changeBoolean(SessionUser(context).getIdFromUser())
        }

        return view
    }

}