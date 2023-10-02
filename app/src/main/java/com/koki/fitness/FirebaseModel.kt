package com.koki.fitness

import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class FirebaseModel {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun appLogin(email: String, password:String, onCompleteListener: OnCompleteListener<AuthResult>) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(onCompleteListener)
    }

    fun appRegister(email: String, password: String, onCompleteListener: OnCompleteListener<AuthResult>) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(onCompleteListener)
    }


    fun appProfile(user: FirebaseUser, username: String, uri: Uri, onCompleteListener: OnCompleteListener<Void>) {

        val profileUpdates = userProfileChangeRequest {
            displayName = username
            photoUri = uri

        }

        user.updateProfile(profileUpdates)
            .addOnCompleteListener(onCompleteListener)
    }

    fun uploadAvatar(uid: String, file: ByteArray, onCompleteListener: OnCompleteListener<UploadTask.TaskSnapshot>) {
        val ref = storage.reference.child("avatars/$uid.jpg")
        val uploadTask = ref.putBytes(file).addOnCompleteListener(onCompleteListener)

    }

    fun uploadPhoto(uid: String, file: ByteArray, onCompleteListener: OnCompleteListener<UploadTask.TaskSnapshot>) {
        val ref = storage.reference.child("locations/$uid.jpg")
        val uploadTask = ref.putBytes(file).addOnCompleteListener(onCompleteListener)

    }

    fun createUser(uid: String, firstName: String, lastName: String, phoneNo: String,
                   age: Number, height: Number, sex: String, weight: Number, onCompleteListener: OnCompleteListener<Void>) {

        var data = hashMapOf<String, Any>("firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to phoneNo,
        "age" to age,
        "height" to height,
        "sex" to sex,
        "weight" to weight)
        db.collection("users").document(uid).set(data
        ).addOnCompleteListener(onCompleteListener)
    }

    fun addPoints(username: String, id: String, onCompleteListener: OnCompleteListener<DocumentReference> ) {
        var data = hashMapOf<String, Any>("username" to username, "points" to 0)
        db.collection("points").document(id).set(data)
    }

    fun earnPoints(uid: String, add: Double, onCompleteListener: OnCompleteListener<DocumentReference> ) {
        db.collection("points").document(uid).update("points", FieldValue.increment(add))
    }

    fun addComment(uid: String, username: String, date: Timestamp, comment: String, onCompleteListener: OnCompleteListener<DocumentReference>) {

        var data = hashMapOf<String, Any>("username" to username,
            "date" to date,
            "comment" to comment
        )

        db.collection("comments").document(uid).collection("post").add(data)

    }

    fun addObject(type: String, desc: String, loc: GeoPoint, author: String, lvl : String, date: Timestamp, onCompleteListener: OnCompleteListener<DocumentReference> ) {
        var data = hashMapOf<String, Any>("type" to type,
            "desc" to desc,
            "author" to author,
            "dateAdded" to date,
            "fitnessLvl" to lvl,
            "location" to loc)
        db.collection("locations").add(data).addOnCompleteListener(onCompleteListener)
    }

    data class User(
        val id: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val phoneNumber: String? = null,
        val age: Number? = null,
        val height: Number? = null,
        val weight: Number? = null,
        val sex: String? = null
    )

    data class Point(
        val id: String? = null,
        val username: String? = null,
        val points: Number? = null
    )

    data class Comment(
        val id : String? = null,
        val username : String? = null,
        val date : Timestamp? = null,
        val text : String? = null
    )

    data class Object(
        val id: String? = null,
        val type: String? = null,
        val desc: String? = null,
        val author: String? = null,
        val lvl: String? = null,
        val date: Timestamp? = null,
        val loc: GeoPoint? = null
    ) : java.io.Serializable




}