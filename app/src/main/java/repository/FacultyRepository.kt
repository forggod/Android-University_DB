package repository

import androidx.room.Room
import androidx.lifecycle.MutableLiveData
import com.example.second34_2.Second34_2Application
import com.example.second34_2.data.*
import com.example.second34_2.database.UniversityDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FacultyRepository private constructor() {
    var university: MutableLiveData<List<Faculty>> = MutableLiveData()
    var faculty: MutableLiveData<List<Group>> = MutableLiveData()

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

    val universityDao = db.getDao()

    suspend fun newFaculty(name: String) {
        val faculty = Faculty(id = null, name = name)
        withContext(Dispatchers.IO) {
            universityDao.insertNewFaculty(faculty)
            university.postValue(universityDao.loadFaculty())
        }
    }

    suspend fun loadUniversity() {
        withContext(Dispatchers.IO) {
            university.postValue(universityDao.loadFaculty())
        }
    }

    suspend fun loadFacultyGroups(facultyId: Int) {
        withContext(Dispatchers.IO) {
            faculty.postValue(universityDao.loadFacultyGroups(facultyId))
        }
    }

    suspend fun getFaculty(facultyId: Int): Faculty? {
        var f: Faculty? = null
        val job = CoroutineScope(Dispatchers.IO).launch {
            f = universityDao.getFaculty(facultyId)
        }
        job.join()
        return f
    }

    suspend fun getGroupStudents(groupId: Long): List<Student> {
        var f: List<Student> = emptyList()
        val job = CoroutineScope(Dispatchers.IO).launch {
            f = universityDao.loadGroupStudents(groupId)
        }
        job.join()
        return f
    }

    /*fun newFaculty(name: String) {
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
     */

}