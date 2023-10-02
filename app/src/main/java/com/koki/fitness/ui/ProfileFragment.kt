package com.koki.fitness.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.koki.fitness.DataViewModel
import com.koki.fitness.FirebaseModel
import com.koki.fitness.LoginActivity
import com.koki.fitness.R


class ProfileFragment : Fragment() {

    private val viewModel: DataViewModel by activityViewModels()
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val fb = FirebaseModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val imageView : ImageView = view.findViewById(R.id.objectPhoto)


        val textView : TextView = view.findViewById(R.id.firstText)
        textView.setText(user!!.displayName)

        val ageText : TextView = view.findViewById(R.id.ageText)
        val sexText : TextView = view.findViewById(R.id.sexText)
        val heightText : TextView = view.findViewById(R.id.heightText)
        val weightText : TextView = view.findViewById(R.id.weightText)

        val outButton : Button = view.findViewById(R.id.logButton)

        outButton.setOnClickListener{
            fb.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        viewModel.getAvatar(user!!.uid)

        viewModel.avatarData.observe(viewLifecycleOwner, Observer { data ->
            imageView.setImageBitmap(data)
        })

        viewModel.getUser(user!!.uid)

        viewModel.profileData.observe(viewLifecycleOwner, Observer {data ->
            ageText.setText(data.age.toString())
            sexText.setText(data.sex)
            heightText.setText(data.height.toString())
            weightText.setText(data.weight.toString())
    })

        return view
    }


}