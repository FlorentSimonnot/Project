package com.example.Tutorial

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class SliderPageFragment(val listFm : List<Fragment>, val fm : FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return listFm[position]
    }

    override fun getCount(): Int {
        return listFm.size
    }
}