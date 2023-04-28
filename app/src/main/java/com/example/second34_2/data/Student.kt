package com.example.second34_2.data

import androidx.room.*

@Entity(
    tableName = "student",
    indices = [Index("last_name", "first_name", "middle_name")],
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
        )
    ]
)
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?,
    @ColumnInfo(name = "middle_name") val middleName: String?,
    val phone: String?,
    @ColumnInfo(name = "birth_date") val birthDate: Long?,
    @ColumnInfo(name = "group_id") val groupId: Int,
)