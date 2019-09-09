package com.example.events

import android.content.Context
import com.example.project.R

enum class Privacy {
    PUBLIC {
        override fun namePrivacy(context : Context): String {
            return context.resources.getString(R.string.event_public)
        }
    },
    PRIVATE {
        override fun namePrivacy(context: Context): String {
            return context.resources.getString(R.string.event_private)
        }
    },
    GUESS {
        override fun namePrivacy(context: Context): String {
            return  context.resources.getString(R.string.event_invitation)
        }
    },
    INIT {
        override fun namePrivacy(context: Context): String {
            return "init"
        }
    };

    fun valueOfString(string : String) : Privacy{
        return when(string){
            "Private" -> PRIVATE
            "PRIVATE" -> PRIVATE
            "Public" -> PUBLIC
            "PUBLIC" -> PUBLIC
            "Only invitation" -> GUESS
            "ONLY INVITATION" -> GUESS
            else -> INIT
        }
    }

    override fun toString(): String {
        return when(this){
            PRIVATE -> "Private"
            PUBLIC -> "Public"
            GUESS -> "Only with invitation"
            else -> {
                throw Exception("Probleme with value of Privacy")
            }
        }
    }

    abstract fun namePrivacy(context: Context) : String
}