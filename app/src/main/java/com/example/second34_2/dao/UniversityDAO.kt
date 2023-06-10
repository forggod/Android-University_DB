package com.example.second34_2.dao

import androidx.room.*
import com.example.second34_2.data.Faculty
import com.example.second34_2.data.Group
import com.example.second34_2.data.Student

@Dao
interface UniversityDAO {
    // *******************************************
    //              About Faculty
    // *******************************************
    @Insert(entity = Faculty::class/*, onConflict = OnConflictStrategy.Replace*/)
    fun insertNewFaculty(faculty: Faculty)

    @Query("DELETE FROM university")
    fun deleteAllFaculty()

    @Query("DELETE FROM university WHERE id = :facultyID")
    fun deleteFacultyByID(facultyID: Int)

    @Delete(entity = Faculty::class)
    fun deleteFaculty(faculty: Faculty)

    @Query("SELECT id, faculty_name FROM university ORDER BY faculty_name")
    fun loadFaculty(): List<Faculty>

    @Update(entity = Faculty::class)
    fun updateFaculty(faculty: Faculty)

    @Query("SELECT id, faculty_name FROM university WHERE id=:id")
    fun getFaculty(id: Int): Faculty

    // *******************************************
    //              About Group
    // *******************************************
    @Insert(entity = Group::class/*, onConflict = OnConflictStrategy.Replace*/)
    fun insertNewGroup(group: Group)

    @Query("DELETE FROM faculty WHERE id = :groupID")
    fun deleteGroupByID(groupID: Int)

    @Delete(entity = Group::class)
    fun deleteGroup(group: Group)

    @Query("SELECT id, group_name, faculty_id FROM faculty ORDER BY group_name")
    fun loadGroup(): List<Group>

    @Query("SELECT * FROM faculty WHERE faculty_id =:faculty_id ORDER BY group_name")
    fun loadFacultyGroups(faculty_id: Int): List<Group>

    @Update(entity = Group::class)
    fun updateGroup(group: Group)
    @Query("DELETE FROM faculty")
    fun deleteAllGroups()

    // *******************************************
    //              About Student
    // *******************************************
    @Insert(entity = Student::class/*, onConflict = OnConflictStrategy.Replace*/)
    fun insertNewStudent(student: Student)

    @Query("SELECT * FROM student WHERE group_id =:groupID ORDER BY last_name")
    fun loadGroupStudents(groupID: Int): List<Student>

    @Query("SELECT * FROM student WHERE id =:id")
    fun getStudentByID(id: Int): Student

    @Query("DELETE FROM student WHERE id = :studentID")
    fun deleteStudentByID(studentID: Int)

    @Delete(entity = Student::class)
    fun deleteStudent(student: Student)

    @Query("SELECT * FROM student ORDER BY last_name")
    fun loadStudent(): List<Student>

    @Update
    fun updateStudent(student: Student)
}