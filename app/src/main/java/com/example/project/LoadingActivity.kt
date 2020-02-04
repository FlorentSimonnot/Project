package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.session.SessionUser
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import android.widget.ProgressBar
import android.widget.TextView






class LoadingActivity : AppCompatActivity() {
    private val session = SessionUser(this)
    private lateinit var textView: TextView
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        //configLanguage()
        startApp()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        textView = findViewById(R.id.loading)
        progressBar = findViewById(R.id.splash_screen_progress_bar)

    }

    private fun doWork() {
        var progress = 0
        configLanguage()
        while (progress < 100) {
            try {
                Thread.sleep(50)
                progressBar.progress = progress
                textView.text = "$progress%"
            } catch (e: Exception) {

                e.printStackTrace()
            }

            progress += 1
        }
    }

    private fun startApp() {
        if (!session.isLogin()) {
            val logInIntent = Intent(this, LoginActivity::class.java)
            logInIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            this.startActivity(logInIntent)
            this.overridePendingTransition(0, 0)
        }
        else{
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK).or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            this.startActivity(intent)
            this.overridePendingTransition(0, 0)
        }
    }


    private fun configLanguage(){
        FirebaseDatabase.getInstance().reference.child("parameters/${session.getIdFromUser()}/language").setValue(
            Locale.getDefault().displayLanguage)
    }

}
