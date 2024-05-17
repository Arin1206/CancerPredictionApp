package com.dicoding.asclepius.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.Room.ResultDao
import com.dicoding.asclepius.Room.ResultDatabase
import com.dicoding.asclepius.Room.ResultEntity
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private var userDao: ResultDao?
    private var userDb: ResultDatabase?

    init {
        userDb = ResultDatabase.getDatabase(application)
        userDao = userDb?.resultDao()
    }

    fun insert(imageUri: String, prediction: String?, confidencescore: Float?) {
        viewModelScope.launch {
            val result = ResultEntity(
                imageUri = imageUri,
                prediction = prediction,
                confidenceScore = confidencescore ?: 0f // Mengatasi nullable confidencescore
            )
            userDao?.insertResult(result)
        }
    }

    fun getdatahistory(): LiveData<List<ResultEntity>>? {
        return userDao?.getdata()
    }


}
