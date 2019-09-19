package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.Tutorial.PageFragment1
import com.example.Tutorial.PageFragment2
import com.example.Tutorial.PageFragment3
import com.example.Tutorial.SliderPageFragment
import java.util.ArrayList

class Tutorial : AppCompatActivity(), ViewPager.OnPageChangeListener {

    private lateinit var viewPager : ViewPager
    private lateinit var pagerAdapter : SliderPageFragment
    private lateinit var dotsIndicators : LinearLayout
    private lateinit var dots : ArrayList<RelativeLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        loadDots()

        val list : List<Fragment> = listOf(PageFragment1(), PageFragment2(), PageFragment3())
        viewPager = findViewById(R.id.viewPager2)
        dotsIndicators = findViewById(R.id.pager_dots)
        pagerAdapter = SliderPageFragment(list, supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.addOnPageChangeListener(this)

    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        dots.forEach {
            it.background = ContextCompat.getDrawable(this, R.drawable.page_dot_unselected)
        }
        dots[position].background = ContextCompat.getDrawable(this, R.drawable.page_dot_selected)
    }

    private fun loadDots(){
        dots = ArrayList()
        dots.add(findViewById<RelativeLayout>(R.id.dot_1))
        dots.add(findViewById<RelativeLayout>(R.id.dot_2))
        dots.add(findViewById<RelativeLayout>(R.id.dot_3))
    }

}
