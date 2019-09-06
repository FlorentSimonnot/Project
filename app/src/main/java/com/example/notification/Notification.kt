package com.example.notification

import com.example.dateCustom.DateTime

/**
 * File created by Jonathan CHU on 30/08/19
 */
class Notification(
    var type: Type = Type(),
    var date : String = "",
    var message: String = "",
    var isSeen: Boolean = true,
    var dateTime : DateTime = DateTime()
) {

    override fun toString(): String {
        return """
            type : $type
            message : $message
            isSeen : $isSeen
        """.trimIndent()
    }

}