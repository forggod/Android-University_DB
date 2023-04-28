package com.example.second34_2.data

import androidx.room.*

@Entity(
    tableName = "faculty",
    indices = [Index("group_name")],
    foreignKeys = [
        ForeignKey(
            entity = Faculty::class,
            parentColumns = ["id"],
            childColumns = ["faculty_id"],
        )
    ]
)

data class Group(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "group_name") val name: String?,
    @ColumnInfo(name = "faculty_id") val facultyID: Int,
)