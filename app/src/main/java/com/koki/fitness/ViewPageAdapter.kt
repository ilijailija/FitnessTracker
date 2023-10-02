package com.koki.fitness

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.koki.fitness.ui.LocationsFragment
import com.koki.fitness.ui.MapFragment
import com.koki.fitness.ui.PointsFragment
import com.koki.fitness.ui.ProfileFragment

class ViewPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                MapFragment()
            }
            1->{
                LocationsFragment()
            }
            2->{
                PointsFragment()
            }

            3->{
                ProfileFragment()
            }
            else->{
                Fragment()
            }
        }
    }
}