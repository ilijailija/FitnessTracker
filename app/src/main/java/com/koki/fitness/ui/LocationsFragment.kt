package com.koki.fitness.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koki.fitness.DataViewModel
import com.koki.fitness.R


class LocationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationsAdapter

    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_locations, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = LocationsAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


        viewModel.objectData.observe(this.viewLifecycleOwner, Observer { data ->
            adapter.setData(data)
        })





        return view
    }


}