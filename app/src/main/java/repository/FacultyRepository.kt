package repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.room.Room
import androidx.lifecycle.MutableLiveData
import com.example.second34_2.Second34_2Application
import com.example.second34_2.api.ServerAPI
import com.example.second34_2.data.*
import com.example.second34_2.database.UniversityDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

    suspend fun newGroup(facultyID: Int, name: String) {
        val group = Group(id = null, name = name, facultyID = facultyID)
        withContext(Dispatchers.IO) {
            universityDao.insertNewGroup(group)
            loadFacultyGroups(facultyID)
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

    suspend fun getGroupStudents(groupId: Int): List<Student> {
        var f: List<Student> = emptyList()
        val job = CoroutineScope(Dispatchers.IO).launch {
            f = universityDao.loadGroupStudents(groupId)
        }
        job.join()
        return f
    }

    suspend fun newStudent(
        groupID: Int,
        firstName: String,
        lastName: String,
        middleName: String,
        phone: String,
        date: Long
    ) {
        val student = Student(
            id = null,
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            phone = phone,
            birthDate = date,
            groupId = groupID
        )
        withContext(Dispatchers.IO) {
            universityDao.insertNewStudent(student)
        }
    }

    suspend fun editStudent(student: Student) {
        Log.d(
            "EDIT_STD",
            "${student.id} ${student.groupId} ${student.firstName} ${student.middleName} ${student.lastName} " +
                    "${student.phone} ${student.birthDate}"
        )
        withContext(Dispatchers.IO) {
            universityDao.updateStudent(student)
        }
    }

    private var myServerAPI: ServerAPI? = null


    private fun getAPI() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
        try {
            val url = "192.168.202.241:5050"
            Retrofit.Builder()
                .baseUrl("http://${url}/faculty/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().apply {
                    myServerAPI = create(ServerAPI::class.java)
                }
        } catch (e: Exception) {
            Log.d("EX", "Возникло исключение $e")
        }
    }

    fun getFaculty() {
        if (myServerAPI == null)
            getAPI()
        if (myServerAPI != null) {

            val request = myServerAPI!!.getFaculty()
            request.enqueue(object : Callback<Faculties> {
                override fun onFailure(call: Call<Faculties>, t: Throwable) {
                    Log.d(TAG, "Ошибка получения истории студентов", t)
                }

                override fun onResponse(
                    call: Call<Faculties>,
                    response: Response<Faculties>
                ) {
                    Log.e(TAG, "Получение истории студентов")
                    val faculties = response.body()
                    val facultyList = faculties?.items
                    CoroutineScope(Dispatchers.IO).launch {
                        val job = CoroutineScope(Dispatchers.IO).launch {
                            universityDao.deleteAllFaculty()
                        }
                        job.join()
                        if (facultyList != null) {
                            for (f in facultyList) {
                                universityDao.insertNewFaculty(f)
                            }
                        }
                        loadUniversity()
                    }
                }
            })
        }
    }

    fun getServerFaculty() {
        if (myServerAPI == null) getAPI()
        CoroutineScope(Dispatchers.Main).launch {
            fetchFaculty()
        }
    }

    private suspend fun fetchFaculty() {
        if (myServerAPI != null) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                val r = myServerAPI!!.getFaculty().awaitResponse()
                if (r.isSuccessful) {
                    val job = CoroutineScope(Dispatchers.IO).launch {
                        universityDao.deleteAllFaculty()
                    }
                    job.join()
                    val faculties = r.body()
                    val facultyList = faculties?.items
                    if (facultyList != null) {
                        for (f in facultyList) {
                            universityDao.insertNewFaculty(f)
                        }
                    }
                }
            }
        }
    }


//    suspend fun editStudent(
//        groupID: Int,
//        firstName: String,
//        lastName: String,
//        middleName: String,
//        phone: String,
//        date: Long
//    ) {
//        val student = Student(
//            id = null,
//            firstName = firstName,
//            lastName = lastName,
//            middleName = middleName,
//            phone = phone,
//            birthDate = date,
//            groupId = groupID
//        )
//        val st: Student? = null
//        withContext(Dispatchers.IO) {
//            universityDao.getStudent()
//        }

//        val faculty = u.find { it?.groups?.find { it.id == groupID } != null } ?: return
//        val group = faculty.groups?.find { it.id == groupID } ?: return
//        val _student = group.student.find { it.id == student.id }
//        if (_student == null) {
//            newStudent(groupID, student)
//            return
//        }
//
//        val list = group.student as ArrayList<Student>
//        val i = list.indexOf(_student)
//        list.remove(student)
//        list.add(i, student)
//        group.student = list
//        university.postValue(u)
//    }


//    fun newStudent(groupID: UUID, student: Student) {
//        val u = university.value ?: return
//
//
//        val faculty = u.find { it?.groups?.find { it.id == groupID } != null } ?: return
//        val group = faculty.groups?.find { it.id == groupID }
//        val list: ArrayList<Student> = if (group!!.student.isEmpty())
//            ArrayList()
//        else
//            group.student as ArrayList<Student>
//        list.add(student)
//        group.student = list
//        university.postValue(u)
//    }
//
//    fun deleteStudent(groupID: UUID, student: Student) {
//        val u = university.value ?: return
//        val faculty = u.find { it?.groups?.find { it.id == groupID } != null } ?: return
//        val group = faculty.groups?.find { it.id == groupID }
//        if (group!!.student.isEmpty()) return
//        val list = group.student as ArrayList<Student>
//        list.remove(student)
//        group.student = list
//        university.postValue(u)
//    }
//
//    fun editStudent(groupID: UUID, student: Student) {
//        val u = university.value ?: return
//        val faculty = u.find { it?.groups?.find { it.id == groupID } != null } ?: return
//        val group = faculty.groups?.find { it.id == groupID } ?: return
//        val _student = group.student.find { it.id == student.id }
//        if (_student == null) {
//            newStudent(groupID, student)
//            return
//        }
//
//        val list = group.student as ArrayList<Student>
//        val i = list.indexOf(_student)
//        list.remove(student)
//        list.add(i, student)
//        group.student = list
//        university.postValue(u)
//    }

}