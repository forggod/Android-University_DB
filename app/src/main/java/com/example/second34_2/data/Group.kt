package com.example.second34_2.data

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "faculty",
    indices = [Index("group_name")],
    foreignKeys = [
        ForeignKey(
            entity = Faculty::class,
            parentColumns = ["id"],
            childColumns = ["faculty_id"],
            onDelete = CASCADE
        )
    ]
)

data class Group(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "group_name") val name: String?,
    @ColumnInfo(name = "faculty_id") val facultyID: Int,
)

class Groups {
    @SerializedName("groups")
    lateinit var groups: List<Group>
}