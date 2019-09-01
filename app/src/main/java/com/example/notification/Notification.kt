package com.example.notification

/**
 * File created by Jonathan CHU on 30/08/19
 */
class Notification(
    var type: Type = Type(),
    var message: String = "",
    var isSeen: Boolean = false,
    var date : String = "",
    var time : String = ""
) {

    override fun toString(): String {
        return """
            type : $type
            message : $message
            isSeen : $isSeen
            date : $date
            time : $time
        """.trimIndent()
    }

}