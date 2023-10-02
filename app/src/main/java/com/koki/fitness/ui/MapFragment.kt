package com.koki.fitness.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.GeoPoint
import com.koki.fitness.DataViewModel
import com.koki.fitness.FirebaseModel
import com.koki.fitness.R
import java.util.*
import kotlin.collections.HashMap


class
MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val viewModel: DataViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isTrackingLocation = false
    private var isMapReady = false

    private lateinit var loc: Location
    private var currentLocationMarker: Marker? = null

    private var markersListData = listOf<FirebaseModel.Object>()
    private var markerslistIDs = mutableListOf<String>()
    private var markers = mutableListOf<Marker>()

    private val markerMap = HashMap<Marker, FirebaseModel.Object>()
    private val markerIDs = HashMap<String, FirebaseModel.Object>()

    private var icon : BitmapDescriptor? = null

    private var author: String? = null
    private var types : MutableList<String> = ArrayList<String>()
    private var lvls : MutableList<String> = ArrayList<String>()
    private var date : Date? = Calendar.getInstance().time
    private var radius : Number? = 0
    private var appliedFilter: Boolean = false






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val MapSupportFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        if (MapSupportFragment != null) {

            MapSupportFragment.getMapAsync(this)
        }

        val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_icon)
        val resizedBitmap = Bitmap.createScaledBitmap(iconBitmap, 150, 150, false)
        icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?.lastLocation?.let { location ->
                    loc = location
                    updateLocationOnMap(location)
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    enableMyLocation()
                }
            }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val addButton : Button


        addButton = view.findViewById(R.id.addButton)

        addButton.setOnClickListener{
            if (isLocationPermissionGranted()) {

                val fm : FragmentManager = childFragmentManager
                val transaction = fm.beginTransaction()

                val args = Bundle()
                args.putDouble("lat", loc.latitude)
                args.putDouble("lng", loc.longitude)

                val frg = AddLocationFragment()
                frg.arguments = args

                transaction.replace(R.id.`object`, frg)
                transaction.addToBackStack("add")
                transaction.commit()
            }
        }

        val filterButton = view.findViewById<Button>(R.id.filterButton)

        filterButton.setOnClickListener{
            Log.d(ContentValues.TAG, "PRESSED")
            var dialog : Dialog = Dialog(requireContext(), R.style.Theme_Fitness)
            dialog.setContentView(R.layout.dialog_filter)

            var close : Button = dialog.findViewById(R.id.exitButton)

            close.setOnClickListener{
                dialog.dismiss()
            }

            var clear : Button = dialog.findViewById(R.id.clearButton)

            clear.setOnClickListener{
                dialog.dismiss()
                clearFilters()
            }

            var apply: Button = dialog.findViewById(R.id.filterButton)

            apply.setOnClickListener{

                author = null
                types = ArrayList<String>()
                lvls = ArrayList<String>()
                date = Calendar.getInstance().time

                val authorText : EditText = dialog.findViewById<EditText>(R.id.authorEditText)
                if (!authorText.text.isEmpty()) {
                    author = authorText.text.toString()
                }
                val calendar : DatePicker = dialog.findViewById<DatePicker>(R.id.calendarView)


                val tmp = Calendar.getInstance()
                tmp.set(calendar.year, calendar.month, calendar.dayOfMonth)
                date = tmp.time




                if (dialog.findViewById<CheckBox>(R.id.easyCheckBox).isChecked) {
                    Log.d(ContentValues.TAG, "YESCHECKED")
                    if (lvls != null) {
                        lvls.add("Beginner")
                    }
                }

                if (dialog.findViewById<CheckBox>(R.id.mediumCheckBox).isChecked) {
                    if (lvls != null) {
                        lvls.add("Intermediate")
                    }
                }

                if (dialog.findViewById<CheckBox>(R.id.hardCheckBox).isChecked) {
                    if (lvls != null) {
                        lvls.add("Advanced")
                    }
                }

                if (dialog.findViewById<CheckBox>(R.id.parkCheckbox).isChecked) {
                    if (types != null) {
                        types.add("Park")
                    }
                }

                if (dialog.findViewById<CheckBox>(R.id.gymCheckBox).isChecked) {
                    if (types != null) {
                        types.add("Gym")
                    }
                }

                if (dialog.findViewById<CheckBox>(R.id.sportCheckBox).isChecked) {
                    if (types != null) {
                        types.add("Sport's Club")
                    }
                }


                dialog.dismiss()
                filterMarkers()


            }

            dialog.show()
        }

        val clearBtn : Button = view.findViewById(R.id.clearButton)

        clearBtn.setOnClickListener{
            clearFilters()

        }

        val radBtn : Button = view.findViewById(R.id.radiusButton)

        radBtn.setOnClickListener{
            radius = 0
            var dialog : Dialog = Dialog(requireContext(), R.style.Theme_Fitness)
            dialog.setContentView(R.layout.dialog_radius)

            val closeBtn : Button = dialog.findViewById(R.id.closeButton)
            val editText : EditText = dialog.findViewById(R.id.editText)
            val applyButton : Button = dialog.findViewById(R.id.FinishButton)

            closeBtn.setOnClickListener{
                dialog.dismiss()
            }

            applyButton.setOnClickListener{
                val tmp = editText.text.toString().toInt() as Number
                radius = tmp
                dialog.dismiss()
                radiusFilter()

            }

            dialog.show()
        }





        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        isMapReady = true

        viewModel.getObjects()

        viewModel.objectData.observe(viewLifecycleOwner, Observer { data ->
            if (data != null) {
                markersListData = data
                Log.d(ContentValues.TAG, "read data")
                getMarkers()
            } else {

            }
        })

        googleMap.setOnMarkerClickListener { marker ->


            val dist: FloatArray = FloatArray(2)

            Location.distanceBetween(
                marker.position.latitude,
                marker.position.longitude,
                loc.latitude,
                loc.longitude,
                dist
            )

            if (!marker.title.equals("User")) {


                val tmp: FirebaseModel.Object = markerIDs[marker.title]!!

                val fm: FragmentManager = childFragmentManager
                val transaction = fm.beginTransaction()

                val bundle: Bundle = Bundle()
                bundle.putSerializable("data", tmp)

                if (dist[0] <= 50) {
                    bundle.putDouble("extra", 3.00)
                }

                val showClueFragment = ShowLocationFragment()
                showClueFragment.arguments = bundle

                transaction.replace(R.id.`object`, showClueFragment)
                transaction.addToBackStack("addClue")
                transaction.commit()
            }

            true
        }



    }

    private fun getMarkers() {

        var i = 0
        for (d in markersListData) {

            if (d.id !in markerslistIDs) {
                val latlng : LatLng = LatLng(d.loc!!.latitude, d.loc!!.longitude)
                val markerOptions = MarkerOptions().position(latlng).title(d.type).title(d.id).icon(icon)

                val temp = mMap.addMarker(markerOptions)
                markerMap[temp!!] = d
                markerIDs[d.id!!] = d
                i++
                markerslistIDs.add(d.id!!)
            }
        }
    }

    private fun checkFilter(data: FirebaseModel.Object) : Boolean {
        val a = author == null || data.author == author
        val l = lvls!!.isEmpty() || data.lvl.toString() in lvls
        val t = types!!.isEmpty() || data.type in types
        val temp : Date = data.date!!.toDate()
        Log.d(ContentValues.TAG, date.toString())
        val da = temp.before(date)


        return a && l && t && da
    }

    private fun filterMarkers() {
        appliedFilter = true
        for ((marker, data) in markerMap) {
            val shouldShowMarker = checkFilter(data)


            if (marker != null) {
                marker.isVisible = shouldShowMarker
            }

        }
    }

    private fun checkRadius(data: FirebaseModel.Object): Boolean {

        val dist: FloatArray = FloatArray(2)

        Location.distanceBetween(
            data.loc!!.latitude,
            data.loc!!.longitude,
            loc.latitude,
            loc.longitude,
            dist
        )


        val r = radius as Int


        return (r >= dist[0])
    }

    private fun radiusFilter() {
        for((marker, data) in markerMap) {
            val shouldShowMarker = checkRadius(data)

            if (marker != null) {
                if (appliedFilter == true) {
                    if (marker.isVisible == true) {
                        marker.isVisible = shouldShowMarker
                    }
                }
                else {
                    marker.isVisible = shouldShowMarker
                }
            }
        }
    }

    private fun clearFilters() {
        appliedFilter = false
        for ((marker, data) in markerMap) {


            if (marker != null) {
                marker.isVisible = true
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isLocationPermissionGranted()) {
            if (isMapReady) {
                mMap.isMyLocationEnabled = true
            }
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (isLocationPermissionGranted()) {
            val locationRequest = LocationRequest.create().apply {
                interval = 10000 // Update location every 10 seconds (adjust as needed)
                fastestInterval = 5000 // Fastest update interval
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                smallestDisplacement = 20.toFloat()
            }


            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            isTrackingLocation = true
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isTrackingLocation = false
    }

    private fun updateLocationOnMap(location: Location) {

        val latLng = LatLng(location.latitude, location.longitude)
        currentLocationMarker?.remove()
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        currentLocationMarker  = mMap.addMarker(MarkerOptions().position(latLng).title("User"))
    }

    override fun onResume() {
        super.onResume()
        if (isTrackingLocation && isLocationPermissionGranted()) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun isLocationPermissionGranted() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED



}