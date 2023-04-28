package database

import androidx.room.Database
import androidx.room.RoomDatabase

import com.example.second34_2.data.Group
import com.example.second34_2.data.Student
import com.example.second34_2.dao.UniversityDAO
import com.example.second34_2.data.Faculty

@Database(
    version = 1,
    entities = [
        Faculty::class,
        Group::class,
        Student::class
    ]
)

abstract class UniversityDatabase: RoomDatabase() {
    abstract fun getDao(): UniversityDAO
}