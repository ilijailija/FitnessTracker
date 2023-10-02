package com.koki.fitness

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.rpc.context.AttributeContext.Auth

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel : AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val loginButton : Button = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailInput)
            val emailText = email.text.toString()
            val password = findViewById<EditText>(R.id.passwordInput)
            val passwordText = password.text.toString()

            if (emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                viewModel.loginUser(emailText, passwordText)
            }
        }

        val registerButton : TextView = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity((intent))
        }

        viewModel.loginData.observe(this, Observer { loginSuccess  ->
            if (loginSuccess == true ) {

                val intent = Intent(this, MainActivity::class.java)

                startActivity((intent))
                finish()
            } else {
                Toast.makeText(getApplicationContext(),"Error logging in", Toast.LENGTH_SHORT).show()
            }
        })



    }
}