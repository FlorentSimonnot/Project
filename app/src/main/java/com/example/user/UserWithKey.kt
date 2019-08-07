package com.example.user

import com.example.session.SessionUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable

data class UserWithKey(
    val user : User = User(),
    val key : String? = ""
): Serializable {

    fun addFriend(session: SessionUser) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${this.key}/friends/${session.getIdFromUser()}")
        ref.setValue("waiting")
        /*ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(User::class.java)
                if (value != null) {
                    value.friends.put(session.getIdFromUser(), "waiting")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })*/
    }

}