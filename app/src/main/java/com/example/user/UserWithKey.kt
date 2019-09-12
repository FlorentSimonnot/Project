package com.example.user

import android.content.Context
import android.widget.Toast
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
        val ref = FirebaseDatabase.getInstance().getReference("friends/$key/${session.getIdFromUser()}")
        ref.child("status").setValue("waiting")
    }

    fun deleteFriend(ctx: Context, sessionUser: SessionUser) {
        val ref = FirebaseDatabase.getInstance().getReference("friends/${sessionUser.getIdFromUser()}/$key")
        ref.removeValue()
        Toast.makeText(ctx, "You have delete this user as friend :(", Toast.LENGTH_SHORT).show()
    }

    fun cancelFriendInvitation(ctx: Context, sessionUser: SessionUser) {
        val ref = FirebaseDatabase.getInstance().getReference("friends/$key/${sessionUser.getIdFromUser()}")
        ref.removeValue()
        Toast.makeText(ctx, "You have delete your request !", Toast.LENGTH_SHORT).show()
    }


}