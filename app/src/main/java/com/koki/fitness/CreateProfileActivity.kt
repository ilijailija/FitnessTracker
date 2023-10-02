package com.koki.fitness

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.IOException


class CreateProfileActivity : AppCompatActivity() {

    private lateinit var viewModel : AuthViewModel
    private var bytes : ByteArray? = null
    private var uri = Uri.EMPTY
    private var spinnerS: Spinner? = null
    private var age : Number = 18
    private var height : Number = 170
    private var weight : Number = 60
    private var sex : String = "m"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        val username = intent.getStringExtra("username")
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val user = Firebase.auth.currentUser



        val agePicker = findViewById<NumberPicker>(R.id.agePicker)
        agePicker.maxValue = 99
        agePicker.minValue = 18
        agePicker.wrapSelectorWheel = true


        agePicker.setOnValueChangedListener { picker, oldVal, newVal ->
            age = newVal
        }

        val weightPicker = findViewById<NumberPicker>(R.id.weightPicker)
        weightPicker.maxValue = 250
        weightPicker.minValue = 30
        weightPicker.wrapSelectorWheel = true

        weightPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            weight = newVal
        }

        val heightPicker = findViewById<NumberPicker>(R.id.heightPicker)
        heightPicker.maxValue = 250
        heightPicker.minValue = 140
        heightPicker.wrapSelectorWheel = true

        heightPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            height = newVal
        }

        spinnerS = findViewById(R.id.spinnerSex)
        val sexes =resources.getStringArray(R.array.spinner_sex)

        if (spinnerS != null) {
            val adapterS =ArrayAdapter(this, android.R.layout.simple_spinner_item, sexes)

            spinnerS!!.adapter = adapterS

            spinnerS!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    sex = "Other"
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    sex = sexes[position]
                }
            }

        }





        val finishButton : Button = findViewById<Button>(R.id.FinishButton)

        finishButton.setOnClickListener {
            val imageView = findViewById<ImageView>(R.id.addPhoto)

            val firstName = findViewById<EditText>(R.id.registerName)
            val firstNameText = firstName.text.toString()

            val lastName = findViewById<EditText>(R.id.registerLast)
            val lastNameText = lastName.text.toString()

            val phoneNumber = findViewById<EditText>(R.id.registerPhNo)
            val phoneNumberText = phoneNumber.text.toString()




            if (firstNameText.isNotEmpty() && lastNameText.isNotEmpty() && phoneNumberText.isNotEmpty()) {
                viewModel.appProfile(user!!,username!!, uri!!)
                viewModel.addPoints(username, user.uid)
                viewModel.createUser(user.uid,firstNameText, lastNameText, phoneNumberText, age, height, sex, weight)

            }
        }

        viewModel.createLiveData.observe(this, Observer { profileSuccess ->
            if (profileSuccess == true) {

                val intent = Intent(this, LoginActivity::class.java)
                viewModel.uploadAvatar(user!!.uid, bytes!!)
                viewModel.uploadAvatar.observe(this, Observer { task ->
                    if (task == true) {
                        startActivity((intent))
                    }
                })
            } else {
                Toast.makeText(getApplicationContext(),"There was an error while registering. Please try later",
                    Toast.LENGTH_SHORT);
            }
        })

    }

    private val getPicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    uri = result.data?.data

                    val imageView = findViewById<ImageView>(R.id.addPhoto)
                    imageView.setImageURI((uri))

                    uri?.let {
                        try {
                            val inputStream = contentResolver.openInputStream(uri!!)
                            if (inputStream != null) {
                                bytes = inputStream.readBytes()
                                inputStream.close()

                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }


    private val getPictureCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {


                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Avatar.jpg")

                val uri = FileProvider.getUriForFile(
                    this,
                    this.applicationContext.packageName + ".provider",
                    file
                )


                val imageView = findViewById<ImageView>(R.id.addPhoto)
                imageView.setImageURI((uri))

                uri?.let {
                    try {
                        val inputStream = contentResolver.openInputStream(uri!!)
                        if (inputStream != null) {
                            bytes = inputStream.readBytes()
                            inputStream.close()

                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

        }


    fun onImageViewClicked(view: View) {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (options[which]) {
                "Take Photo" -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val file: File = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "Avatar.jpg"
                    )

                    uri = FileProvider.getUriForFile(
                        this,
                        this.packageName + ".provider",
                        file
                    )
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    getPictureCamera.launch(takePicture)
                }
                "Choose from Gallery" -> {
                    val pickPhotoIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    getPicture.launch(pickPhotoIntent)
                }
                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

}