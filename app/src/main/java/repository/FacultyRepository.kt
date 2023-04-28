package repository

import androidx.room.Room
import androidx.lifecycle.MutableLiveData
import com.example.second34_2.Second34_2Application
import com.example.second34_2.data.*
import com.example.second34_2.database.UniversityDatabase
import kotlin.collections.ArrayList
import java.util.*

class FacultyRepository private constructor() {
    var university: MutableLiveData<List<Faculty>> = MutableLiveData()

    companion object {
        private var INSTANCE: FacultyRepository? = null

        fun newInstance() {
            if (INSTANCE == null) {
                INSTANCE = FacultyRepository()
            }
        }

        fun get(): FacultyRepository {
            return INSTANCE
                ?: throw java.lang.IllegalStateException("Репозиторий Faculty Repository не иницилизирован")
        }
    }

    val db = Room.databaseBuilder(
        Second34_2Application.applicationContex(),
        UniversityDatabase::class.java, "uniDB.db"
    ).build()


    fun newFaculty(name: String) {
        val faculty = Faculty(name = name)
        val list: ArrayList<Faculty> =
            if (university.value != null) {
                (university.value as ArrayList<Faculty>)
            } else
                ArrayList<Faculty>()
        list.add(faculty)
        university.postValue(list)
    }

    fun newGroup(facultyID: UUID, name: String) {
        val u = university.value ?: return
        val faculty = u.find { it.id == facultyID } ?: return
        val group = Group(name = name)
        val list: ArrayList<Group> = if (faculty.groups.isEmpty())
            ArrayList()
        else
            faculty.groups as ArrayList<Group>
        list.add(group)
        faculty.groups = list
        university.postValue(u)
    }

    fun newStudent(groupID: UUID, student: Student) {
        val u = university.value ?: return


        val faculty = u.find { it?.groups?.find { it.id == groupID } != null } ?: return
        val group = faculty.groups?.find { it.id == groupID }
        val list: ArrayList<Student> = if (group!!.student.isEmpty())
            ArrayList()
        else
            group.student as ArrayList<Student>
        list.add(student)
        group.student = list
        university.postValue(u)
    }

    fun deleteStudent(groupID: UUID, student: Student) {
        val u = university.value ?: return
        val faculty = u.find { it?.groups?.find { it.id == groupID } != null } ?: return
        val group = faculty.groups?.find { it.id == groupID }
        if (group!!.student.isEmpty()) return
        val list = group.student as ArrayList<Student>
        list.remove(student)
        group.student = list
        university.postValue(u)
    }

    fun editStudent(groupID: UUID, student: Student) {
        val u = university.value ?: return
        val faculty = u.find { it?.groups?.find { it.id == groupID } != null } ?: return
        val group = faculty.groups?.find { it.id == groupID } ?: return
        val _student = group.student.find { it.id == student.id }
        if (_student == null) {
            newStudent(groupID, student)
            return
        }

        val list = group.student as ArrayList<Student>
        val i = list.indexOf(_student)
        list.remove(student)
        list.add(i, student)
        group.student = list
        university.postValue(u)
    }
/*
}