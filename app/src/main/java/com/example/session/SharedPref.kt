package com.example.session

import android.content.Context


class SharedPref(val context: Context) {
    private val sharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    // this method will save the nightMode State : True or False
    fun setNightModeState(state: Boolean?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("NightMode", state!!)
        editor.commit()
    }

    // this method will load the Night Mode State
    fun loadNightModeState(): Boolean? {
        return sharedPreferences.getBoolean("NightMode", false)
    }
}