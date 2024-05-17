package com.dicoding.asclepius.Room

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "result_table")
@Parcelize

data class ResultEntity(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int = 0,
    val imageUri: String?,
    val prediction: String?,
    val confidenceScore: Float?
) : Parcelable
