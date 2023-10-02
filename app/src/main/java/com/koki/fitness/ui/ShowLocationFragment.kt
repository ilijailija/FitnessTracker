package com.koki.fitness.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.koki.fitness.DataViewModel
import com.koki.fitness.FirebaseModel
import com.koki.fitness.R
import java.text.SimpleDateFormat
import java.util.*


class ShowLocationFragment : Fragment() {
    private var locationObject : FirebaseModel.Object? = null
    private val viewModel: DataViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentsAdapter
    private val comment: String? = null
    private var user : FirebaseUser? = null
    private var extra : Double = 1.00
    private var storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        locationObject = args!!.getSerializable("data") as FirebaseModel.Object
        if (args.getDouble("extra") != 0.0) {
            extra = args.getDouble("extra")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_location, container, false)


        viewModel.getComments(locationObject?.id!!)

        viewModel.userData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { data ->
            user = data
        })

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CommentsAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.commentData.observe(this.viewLifecycleOwner, androidx.lifecycle.Observer { data ->
            Log.d(ContentValues.TAG, "DATAAA")
            adapter.setData(data.reversed())
        })

        val typeView : TextView = view.findViewById<TextView>(R.id.typeText)
        val lvlView : TextView = view.findViewById(R.id.lvlText)
        val dateView : TextView = view.findViewById(R.id.dateText)
        val descView : TextView = view.findViewById(R.id.descText)
        val imageView : ImageView = view.findViewById(R.id.objectPhoto)

        val id = locationObject!!.id

        val tmp = locationObject!!.date!!.toDate()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        typeView.setText(locationObject!!.type.toString())
        lvlView.setText(locationObject!!.lvl.toString())
        dateView.setText(dateFormat.format(tmp).toString())
        descView.setText(locationObject!!.desc!!.toString())

        viewModel.getPhoto(id!!)


        viewModel.photoData.observe(viewLifecycleOwner, androidx.lifecycle.Observer  { data ->
            imageView.setImageBitmap(data)
        })

        val addButton : Button = view.findViewById<Button>(R.id.addButton)

        addButton.setOnClickListener{
            val txt : TextView = view.findViewById(R.id.commentBox)
            var mult = 1
            if (locationObject!!.lvl == "Intermediate") {
                mult = 2
            }
            if (locationObject!!.lvl == "Advanced") {
                mult = 3
            }
            viewModel.addComment(locationObject!!.id!!, user!!.displayName!!, Timestamp(Date()), txt.text.toString())
            Log.d(ContentValues.TAG, extra.toString())
            viewModel.earnPoints(user!!.uid, 5.00 * mult * extra)
            txt.setText("")
        }

        val closeButton : Button = view.findViewById(R.id.closeButton)

        closeButton.setOnClickListener{
            getParentFragmentManager().popBackStack();
        }


        return view
    }

}