package com.dicoding.asclepius.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResultDao {
    @Insert
    suspend fun insertResult(result: ResultEntity)

    @Query("SELECT * FROM result_table")
    fun getdata(): LiveData<List<ResultEntity>>

}
