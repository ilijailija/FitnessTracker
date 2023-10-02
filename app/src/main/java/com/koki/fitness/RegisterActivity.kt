package com.koki.fitness

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel : AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val registerButton : Button = findViewById<Button>(R.id.RegisterButton)
        var userID = ""
        var usernameText = ""

        registerButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.email)
            val emailText = email.text.toString()
            val password = findViewById<EditText>(R.id.password)
            val passwordText = password.text.toString()
            val username = findViewById<EditText>(R.id.username)
            usernameText = username.text.toString()

            val cpassword = findViewById<EditText>(R.id.cpassword)
            val cpasswordText = password.text.toString()


            if (passwordText != cpasswordText) {
                cpassword.setBackgroundColor(Color.parseColor("#FF0000"))
            }

            if (usernameText.isNotEmpty() && emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                viewModel.registerUser(emailText,passwordText)
            }
        }

        viewModel.registerLiveData.observe(this, Observer { user ->
            if (user != null) {

                val intent = Intent(this, CreateProfileActivity::class.java)
                intent.putExtra("username", usernameText)
                startActivity((intent))
            } else {
                Toast.makeText(getApplicationContext(),"There was an error while registering. Please try later",
                    Toast.LENGTH_SHORT);
            }
        })

    }
}