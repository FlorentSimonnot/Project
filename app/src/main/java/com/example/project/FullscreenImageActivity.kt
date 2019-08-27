package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.utils.Image
import com.example.utils.Utils

class FullscreenImageActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var fullscreenImage : ImageView
    private lateinit var dateTextView : TextView
    private lateinit var timeTextView : TextView
    private lateinit var toolbar : Toolbar
    private lateinit var footer : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fullscreenImage = findViewById(R.id.fullscreenImage)
        dateTextView = findViewById(R.id.dateImage)
        timeTextView = findViewById(R.id.timeImage)
        footer = findViewById(R.id.footer)

        val info = intent.extras
        val image = info?.getSerializable("image") as Image

        fullscreenImage.setOnClickListener(this)

        image.showImage(fullscreenImage)
        image.showDate(dateTextView)
        image.showTime(timeTextView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.fullscreenImage -> {
                if(toolbar.visibility == View.GONE){
                    toolbar.visibility = View.VISIBLE
                    footer.visibility = View.VISIBLE
                }else{
                    toolbar.visibility = View.GONE
                    footer.visibility = View.GONE
                }
            }
        }
    }
}
