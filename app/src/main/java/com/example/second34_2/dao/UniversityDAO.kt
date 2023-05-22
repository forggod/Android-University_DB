package com.example.second34_2.dao

import androidx.room.*
import com.example.second34_2.data.Faculty
import com.example.second34_2.data.Group
import com.example.second34_2.data.Student

@Dao
interface UniversityDAO {
    @Insert(entity = Faculty::class/*, onConflict = OnConflictStrategy.Replace*/)
    fun insertNewFaculty(faculty: Faculty)

    @Query("DELETE FROM university WHERE id = :facultyID")
    fun deleteFacultyByID(facultyID: Long)

    @Delete(entity = Faculty::class)
    fun deleteFaculty(faculty: Faculty)

    @Query("SELECT id, faculty_name FROM university ORDER BY faculty_name")
    fun loadFaculty(): List<Faculty>

    @Update(entity = Faculty::class)
    fun updateFaculty(faculty: Faculty)

    @Insert(entity = Group::class/*, onConflict = OnConflictStrategy.Replace*/)
    fun insertNewGroup(group: Group)

    @Query("DELETE FROM faculty WHERE id = :groupID")
    fun deleteGroupByID(groupID: Long)

    @Delete(entity = Group::class)
    fun deleteGroup(group: Group)

    @Query("SELECT id, group_name, faculty_id FROM faculty ORDER BY group_name")
    fun loadGroup(): List<Group>

    @Query("SELECT * FROM faculty WHERE faculty_id =:faculty_id ORDER BY group_name")
    fun loadFacultyGroups(faculty_id: Int): List<Group>

    @Query("SELECT * FROM student WHERE group_id =:groupID ORDER BY last_name")
    fun loadGroupStudents(groupID: Long): List<Student>

    @Query("SELECT id, faculty_name FROM university WHERE id=:id")
    fun getFaculty(id: Int): Faculty

    @Update(entity = Group::class)
    fun updateGroup(group: Group)

    @Insert(entity = Student::class/*, onConflict = OnConflictStrategy.Replace*/)
    fun insertNewStudent(student: Student)

    @Query("SELECT * FROM student WHERE id =:id")
    fun getStudentByID(id: Int) :Student

    @Query("DELETE FROM student WHERE id = :studentID")
    fun deleteStudentByID(studentID: Long)

    @Delete(entity = Student::class)
    fun deleteStudent(student: Student)

    @Query("SELECT * FROM student ORDER BY last_name")
    fun loadStudent(): List<Student>

    @Update(entity = Student::class)
    fun updateStudent(student: Student)
}