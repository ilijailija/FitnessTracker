package com.koki.fitness

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val fbModel = FirebaseModel()

    private val _registerLiveData = MutableLiveData<FirebaseUser?>()
    val registerLiveData: LiveData<FirebaseUser?>
        get() = _registerLiveData

    private val _loginData = MutableLiveData<Boolean?>()
    val loginData: LiveData<Boolean?>
        get() = _loginData

    private val _ProfileLiveData = MutableLiveData<Boolean?>()
    val ProfileLiveData: LiveData<Boolean?>
        get() = _ProfileLiveData

    private val _pointsLiveData = MutableLiveData<Boolean?>()
    val pointsLiveData: LiveData<Boolean?>
        get() = _pointsLiveData

    private val _uploadAvatar = MutableLiveData<Boolean>()
    val uploadAvatar : LiveData<Boolean>
        get() = _uploadAvatar

    private val _createLiveData = MutableLiveData<Boolean?>()
    val createLiveData: LiveData<Boolean?>
        get() = _createLiveData



    fun registerUser(email: String, password: String) {
        fbModel.appRegister(email, password, OnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result?.user
                _registerLiveData.value = user

            } else {
                _registerLiveData.value = null
            }
        })
    }

    fun loginUser(email: String, password: String) {
        fbModel.appLogin(email, password, OnCompleteListener { task ->
            if (task.isSuccessful) {
                _loginData.value = true

            } else {
                _loginData.value = false
            }
        })
    }

    fun appProfile(user: FirebaseUser, username: String, uri: Uri) {

        fbModel.appProfile(user,username, uri, OnCompleteListener { task ->
            if (task.isSuccessful) {
                _ProfileLiveData.value = true

            } else {
                _ProfileLiveData.value = false
            }
        })
    }

    fun createUser(uid: String, firstName: String, lastName: String, phoneNo: String,
                   age: Number, height: Number, sex: String, weight: Number) {

        fbModel.createUser(uid, firstName, lastName, phoneNo, age, height, sex, weight, OnCompleteListener {task ->
            if (task.isSuccessful) {
                _createLiveData.value = true
            }
            else {
                _createLiveData.value = false
            }
        })

    }

    fun addPoints(username: String, id: String) {
        fbModel.addPoints(username, id, OnCompleteListener {task ->
            if (task.isSuccessful) {
                _pointsLiveData.value = true
            }
            else {
                _pointsLiveData.value = false
            }
        })}

    fun uploadAvatar(uid:String, file:ByteArray) {
        fbModel.uploadAvatar(uid, file, OnCompleteListener { task-> _uploadAvatar.value = task.isSuccessful })
    }

}