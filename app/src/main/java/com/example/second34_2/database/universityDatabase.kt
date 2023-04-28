package com.example.second34_2.database

import androidx.room.*
import com.example.second34_2.dao.*
import com.example.second34_2.data.*


@Database(
    version = 1,
    entities = [
        Faculty::class,
        Group::class,
        Student::class,
    ]
)

abstract class UniversityDatabase : RoomDatabase() {
    abstract fun getDao(): UniversityDAO
}