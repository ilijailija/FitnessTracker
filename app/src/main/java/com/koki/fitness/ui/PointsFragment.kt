package com.koki.fitness.ui

import android.os.Bundle
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


class PointsFragment : Fragment() {

    private val viewModel: DataViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PointsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_points, container, false)

        viewModel.getPoints()

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = PointsAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.pointsData.observe(viewLifecycleOwner, Observer { newData->
            adapter.setData(newData.reversed())
        })


        return view
    }


}