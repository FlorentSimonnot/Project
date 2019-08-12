package com.example.notification
import androidx.annotation.NonNull
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import java.util.*
import kotlin.collections.HashMap

class CloudFunction {
    private var function : FirebaseFunctions = FirebaseFunctions.getInstance()

    fun acceptParticipation(keyEvent : String, keyUser :String){
        val data : HashMap<String, Any> = HashMap<String, Any>()
        data.put("key", keyEvent)
        data.put("user", keyUser)
        data.put("push", true)

    }
}