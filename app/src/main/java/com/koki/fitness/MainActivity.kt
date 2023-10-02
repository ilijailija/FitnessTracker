package com.koki.fitness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private val viewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)


        val adapter = ViewPageAdapter(supportFragmentManager,lifecycle)
        viewPager.adapter = adapter
        viewPager.setUserInputEnabled(false)

        TabLayoutMediator(tabLayout,viewPager){tab,position->
            when(position){
                0->{
                    tab.text ="Map"
                }
                1->{
                    tab.text="Locations"
                }
                2->{
                    tab.text="Points"
                }
                3-> {
                    tab.text="Profile"
                }

            }
        }.attach()



    }
}