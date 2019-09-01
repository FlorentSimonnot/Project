package com.example.notification

/**
 * File created by Jonathan CHU on 30/08/19
 */
class Type(
    var typeNotif: TypeNotification = TypeNotification.EVENT,
    var key: String = ""
){
    override fun toString(): String {
        return """
            typeNotification : $typeNotif
            key : $key
        """.trimIndent()
    }
}