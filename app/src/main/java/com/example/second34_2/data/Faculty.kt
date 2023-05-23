package com.example.second34_2.data

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "university")
data class Faculty(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "faculty_name") val name: String?,
)
class Faculties {
    @SerializedName("items")
    lateinit var items : List<Faculty>
}