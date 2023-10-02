package com.koki.fitness.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.firebase.firestore.GeoPoint
import com.koki.fitness.DataViewModel
import com.koki.fitness.R
import java.io.File
import java.io.IOException
import java.util.*


class AddLocationFragment : Fragment() {

    private var lat: Double? = null
    private var lng: Double? = null
    private var loc : GeoPoint? = null
    private var uri = Uri.EMPTY
    private var bytes : ByteArray? = null

    private var spinnerTypes: Spinner? = null
    private var spinnerLvl: Spinner? = null
    private var typeSel: String? = null
    private var lvlSel: String? = null

    private var username: String? = null
    private var uid:String? = null

    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lat = arguments?.getDouble("lat")
        lng = arguments?.getDouble("lng")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_location, container, false)

        spinnerTypes = view.findViewById(R.id.spinnerType)
        spinnerLvl = view.findViewById(R.id.spinnerLvl)

        val types = resources.getStringArray(R.array.spinner_type)
        val lvls = resources.getStringArray(R.array.spinner_lvl)


        if (spinnerTypes != null) {
            val adapterType =
                ArrayAdapter(view.context, android.R.layout.simple_spinner_item, types)
            spinnerTypes!!.adapter = adapterType

            spinnerTypes!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    typeSel = "Gym"
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    typeSel = types[position]
                }
            }
        }

        if (spinnerLvl != null) {
            val adapterLvl = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, lvls)

            spinnerLvl!!.adapter = adapterLvl

            spinnerLvl!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    lvlSel = "Beginner"
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    lvlSel = lvls[position]
                }
            }

        }

        viewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            username = user.displayName
            uid = user.uid
        })

        val image = view.findViewById<ImageView>(R.id.addPhoto)

        image.setOnClickListener(View.OnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

            val builder = AlertDialog.Builder(view.context)
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
                            view.context,
                            view.context.packageName + ".provider",
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
        })


        val finishButton = view.findViewById<Button>(R.id.FinishButton) as Button

        finishButton.setOnClickListener {

            val desc = view.findViewById<EditText>(R.id.objectDesc).text.toString()
            loc = GeoPoint(lat!!, lng!!)
            val date = com.google.firebase.Timestamp(Calendar.getInstance().time)

            viewModel.addObject(typeSel!!,desc, loc!!,username!!,lvlSel!!, date)

            viewModel.addobjectData.observe(viewLifecycleOwner, Observer { success ->

                viewModel.earnPoints(uid!!, 20.0)
                viewModel.uploadPhoto(success.id, bytes!!)
                getParentFragmentManager().popBackStack();


            })


        }

            return view


    }

    private val getPicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    uri = result.data?.data

                    val imageView = requireView().findViewById<ImageView>(R.id.addPhoto)
                    imageView.setImageURI((uri))

                    uri?.let {
                        try {
                            val inputStream = getActivity()?.getContentResolver()?.openInputStream(uri!!)
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
                    requireView().context,
                    requireView().context.applicationContext.packageName + ".provider",
                    file
                )


                val imageView = requireView().findViewById<ImageView>(R.id.addPhoto)
                imageView.setImageURI((uri))

                uri?.let {
                    try {
                        val inputStream = getActivity()?.getContentResolver()?.openInputStream(uri!!)
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