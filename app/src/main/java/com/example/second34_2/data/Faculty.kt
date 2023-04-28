package com.example.second34_2.data

import androidx.room.*

@Entity(tableName = "university")
data class Faculty(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "faculty_name") val name: String?,
)