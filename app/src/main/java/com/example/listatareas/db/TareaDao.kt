package com.example.listatareas.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TareaDao {

    @Query("SELECT * FROM tarea  ORDER BY realizada")
    fun getAll(): List<Tarea>

    @Query("SELECT COUNT(*) FROM tarea")
    fun  contar():Int

    @Insert
    fun  insertTarea(tarea: Tarea):Long

    @Update
    fun updateTarea(tare:Tarea)

    @Delete
    fun deleteTarea(tarea: Tarea)

}