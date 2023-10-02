package com.koki.fitness

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayInputStream

class DataViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val fb = FirebaseModel()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage

    private lateinit var pointsListener: ListenerRegistration
    private lateinit var objectListener: ListenerRegistration
    private lateinit var commentListener : ListenerRegistration

    private val _userData = MutableLiveData<FirebaseUser>()
    val userData: LiveData<FirebaseUser>
        get() = _userData

    private val _objectData = MutableLiveData<List<FirebaseModel.Object>>()
    val objectData : LiveData<List<FirebaseModel.Object>>
        get() = _objectData

    private val _addobjectData = MutableLiveData<DocumentReference>()
    val addobjectData : LiveData<DocumentReference>
        get() = _addobjectData

    private val _pointsData = MutableLiveData<List<FirebaseModel.Point>>()
    val pointsData : LiveData<List<FirebaseModel.Point>>
        get() = _pointsData

    private val _earnPts = MutableLiveData<Boolean>()
    val earnPts: LiveData<Boolean>
        get() = _earnPts

    private val _addComment = MutableLiveData<Boolean>()
    val addComment: LiveData<Boolean>
        get() = _addComment

    private val _commentData = MutableLiveData<List<FirebaseModel.Comment>>()
    val commentData: LiveData<List<FirebaseModel.Comment>>
        get() = _commentData

    private val _photoData = MutableLiveData<Bitmap>()
    val photoData : LiveData<Bitmap>
        get() = _photoData

    private val _avatarData = MutableLiveData<Bitmap>()
    val avatarData : LiveData<Bitmap>
        get() = _avatarData

    private val _profileData = MutableLiveData<FirebaseModel.User>()
    val profileData : LiveData<FirebaseModel.User>
        get() = _profileData




    init {
        _userData.value = firebaseAuth.currentUser
        firebaseAuth.addAuthStateListener { firebaseAuth ->
            _userData.value = firebaseAuth.currentUser
        }
    }

    fun getPoints() {
        pointsListener = db.collection("points").orderBy("points").addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            val tempList = mutableListOf<FirebaseModel.Point>()
            if (querySnapshot != null) {
                for (doc in querySnapshot) {
                    val id = doc.id
                    val points = doc.get("points") as? Number
                    val username = doc.getString("username")
                    val toAdd = FirebaseModel.Point(id, username, points)
                    tempList.add(toAdd)

                }
            }
            _pointsData.value = tempList
        }

    }

    fun getObjects() {
        objectListener = db.collection("locations").addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            val tempList = mutableListOf<FirebaseModel.Object>()
            if (querySnapshot != null) {
                for (doc in querySnapshot) {
                    val id = doc.id
                    val type = doc.getString("type")
                    val desc = doc.getString("desc")
                    val author = doc.getString("author")
                    val lvl = doc.get("fitnessLvl").toString()
                    val date = doc.getTimestamp("dateAdded")
                    val loc = doc.getGeoPoint("location")
                    val toAdd = FirebaseModel.Object(id, type, desc, author, lvl, date, loc)
                    tempList.add(toAdd)

                }
            }
            _objectData.value = tempList
        }
    }

    fun addObject(type: String, desc: String, loc: GeoPoint, author: String, lvl : String, date: Timestamp) {
        fb.addObject(type, desc, loc, author, lvl, date, OnCompleteListener { task ->
            _addobjectData.value = task.result
        })

    }

    fun earnPoints(uid: String, point: Double) {
        fb.earnPoints(uid, point, OnCompleteListener {task->
            _earnPts.value=task.isSuccessful
        })
    }

    fun addComment(uid: String, username: String, date: Timestamp, comment: String) {
        fb.addComment(uid, username, date, comment, OnCompleteListener { task -> _addComment.value = task.isSuccessful })
    }

    fun uploadPhoto(uid: String, file: ByteArray) {
        fb.uploadPhoto(uid, file, OnCompleteListener { task->})

    }

    fun getComments(uid:String) {
        commentListener = db.collection("comments").document(uid).collection("post").orderBy("date").addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            val tempList = mutableListOf<FirebaseModel.Comment>()
            if (querySnapshot != null) {
                for (doc in querySnapshot) {
                    val id = doc.id
                    val username = doc.getString("username")
                    val date = doc.getTimestamp("date")
                    val text = doc.getString("comment")
                    val toAdd = FirebaseModel.Comment(id, username, date, text)
                    tempList.add(toAdd)

                }
            }
            _commentData.value = tempList
        }
    }

    fun getPhoto(uid: String) {
        val ref = storage.reference.child("locations/$uid.jpg")
        ref.getBytes(1024*1024*8).addOnSuccessListener { imageBytes ->

            val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(imageBytes))
            _photoData.value = bitmap

        }.addOnFailureListener { exception ->

        }
    }

    fun getAvatar(uid: String) {
        val ref = storage.reference.child("avatars/$uid.jpg")
        ref.getBytes(1024*1024*10).addOnSuccessListener { imageBytes ->

            val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(imageBytes))
            _avatarData.value = bitmap

        }.addOnFailureListener { exception ->

        }
    }

    fun getUser(uid: String) {
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            if (doc != null) {
                var tmp : FirebaseModel.User

                var age = doc.get("age") as? Number
                var sex = doc.getString("sex")
                var height = doc.get("height") as? Number
                var weight = doc.get("weight") as? Number
                var first = doc.getString("firstName")
                var last = doc.getString("lastName")
                var phno = doc.getString("phoneNumber")

                tmp = FirebaseModel.User(doc.id,first, last, phno, age, height, weight ,sex )
                _profileData.value = tmp

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pointsListener.remove()
        objectListener.remove()
        commentListener.remove()
    }




}