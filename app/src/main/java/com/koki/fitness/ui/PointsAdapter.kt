package com.koki.fitness.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.koki.fitness.FirebaseModel
import com.koki.fitness.R

class PointsAdapter : RecyclerView.Adapter<PointsAdapter.ViewHolder>() {

    private var data : List<FirebaseModel.Point>? = null

    fun setData(newData: List<FirebaseModel.Point>) {
        data = newData
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userView: TextView
        val pointView: TextView
        val numberView : TextView

        init {
            // Define click listener for the ViewHolder's View
            userView = view.findViewById(R.id.usernameTextView)
            pointView = view.findViewById(R.id.pointsTextView)
            numberView = view.findViewById(R.id.numberTextView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.point_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        viewHolder.pointView.text = data?.get(position)?.points.toString() + " points"
        viewHolder.userView.text = data?.get(position)?.username
        viewHolder.numberView.text = (position+1).toString() + "."



    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }
}