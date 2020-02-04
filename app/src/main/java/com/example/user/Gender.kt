package com.example.user

import android.content.Context
import com.example.project.R

enum class Gender {
    Male,
    Female,
    Other;

    fun getString(context : Context) : String{
        return when(this){
            Male -> context.resources.getString(R.string.next_sign_in_male)
            Female -> context.resources.getString(R.string.next_sign_in_female)
            Other -> context.resources.getString(R.string.next_sign_in_other)
        }
    }

}