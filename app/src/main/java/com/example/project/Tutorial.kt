package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.Tutorial.PageFragment1
import com.example.Tutorial.PageFragment2
import com.example.Tutorial.PageFragment3
import com.example.Tutorial.SliderPageFragment
import java.util.ArrayList

class Tutorial : AppCompatActivity() {
    private lateinit var viewPager : ViewPager
    private lateinit var pagerAdapter : SliderPageFragment
    private lateinit var dotsIndicators : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        val list : List<Fragment> = listOf(PageFragment1(), PageFragment2(), PageFragment3())
        viewPager = findViewById(R.id.viewPager2)
        dotsIndicators = findViewById(R.id.pager_dots)
        pagerAdapter = SliderPageFragment(list, supportFragmentManager)
        viewPager.adapter = pagerAdapter

    }

}
