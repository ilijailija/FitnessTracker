package com.koki.fitness.ui

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.koki.fitness.FirebaseModel
import com.koki.fitness.R

class LocationsAdapter : RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    private var data : List<FirebaseModel.Object>? = null
    private var context : Context? = null

    fun setData(newData: List<FirebaseModel.Object>) {
        data = newData
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView
        val descView: TextView
        val locView : TextView

        init {
            nameView = view.findViewById(R.id.userNameTextView)
            descView = view.findViewById(R.id.dateTextView)
            locView = view.findViewById(R.id.commentTextViw)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.location_item, viewGroup, false)

        context = view.context


        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        viewHolder.nameView.text = data?.get(position)?.type.toString()
        viewHolder.descView.text = data?.get(position)?.author.toString()
        viewHolder.locView.text = data?.get(position)?.lvl.toString()


    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }



}