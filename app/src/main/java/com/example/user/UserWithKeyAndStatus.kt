package com.example.user

import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

data class UserWithKeyAndStatus(
    val user : User = User(),
    val key : String? = "",
    val status : String = ""
): Serializable {

}