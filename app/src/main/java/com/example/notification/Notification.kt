package com.example.notification

import com.example.dateCustom.DateTime

/**
 * File created by Jonathan CHU on 30/08/19
 */
class Notification(
    var type: Type = Type(),
    var date : Long = 0,
    var message: String = "",
    var isSeen: Boolean = true
) {

    override fun toString(): String {
        return """
            type : $type \n
            message : $message \n
            isSeen : $isSeen
        """.trimIndent()
    }

}