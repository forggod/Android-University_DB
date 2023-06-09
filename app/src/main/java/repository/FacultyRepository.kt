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

    // *******************************************
    //              About Server API
    // *******************************************
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
            val url = "192.168.0.105:5050"
            Retrofit.Builder()
                .baseUrl("http://${url}/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().apply {
                    myServerAPI = create(ServerAPI::class.java)
                }
        } catch (e: Exception) {
            Log.d("EX", "Возникло исключение $e")
        }
    }

    // *******************************************
    //              About Faculty
    // *******************************************
    suspend fun newFaculty(name: String) {
        val faculty = Faculty(id = null, name = name)
        Log.d("ADD_FAC", "Added new faculty $name")
        withContext(Dispatchers.IO) {
            universityDao.insertNewFaculty(faculty)
        }
        loadUniversity()
    }

    suspend fun loadUniversity() {
        Log.d("GET_FAC", "GET list of faculties")
        withContext(Dispatchers.IO) {
            university.postValue(universityDao.loadFaculty())
        }
    }

    suspend fun getFaculty(facultyId: Int): Faculty? {
        var f: Faculty? = null
        val job = CoroutineScope(Dispatchers.IO).launch {
            f = universityDao.getFaculty(facultyId)
        }
        job.join()
        Log.d("GET_FAC", "GET faculty by ID $facultyId")
        return f
    }

    suspend fun deleteFaculty(facultyId: Int) {
        Log.d("DEL_FAC", "Delete faculty with id $facultyId")
        withContext(Dispatchers.IO) {
            universityDao.deleteFacultyByID(facultyId)
        }
        loadUniversity()
    }

    fun getFaculty() {
        if (myServerAPI == null)
            getAPI()
        if (myServerAPI != null) {
            val request = myServerAPI!!.getFaculty()
            request.enqueue(object : Callback<Faculties> {
                override fun onFailure(call: Call<Faculties>, t: Throwable) {
                    Log.d(TAG, "Ошибка получения истории факультетов", t)
                }

                override fun onResponse(
                    call: Call<Faculties>,
                    response: Response<Faculties>
                ) {
                    Log.e(TAG, "Получение истории факультетов")
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

    fun postFaculty() {
        if (myServerAPI == null)
            getAPI()
        if (myServerAPI != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val request = myServerAPI!!.postFaculty(universityDao.loadFaculty())
                request.enqueue(object : Callback<Faculties> {

                    override fun onFailure(call: Call<Faculties>, t: Throwable) {
                        Log.d(TAG, "Ошибка отправки истории факультетов", t)
                    }

                    override fun onResponse(
                        call: Call<Faculties>,
                        response: Response<Faculties>
                    ) {
                        Log.e(TAG, "Отправка истории факультетов")
                        // TODO: something
                    }
                })
            }
        }
    }

    // *******************************************
    //              About Group
    // *******************************************
    suspend fun newGroup(facultyID: Int, name: String) {
        val group = Group(id = null, name = name, facultyID = facultyID)
        Log.d("ADD_GRP", "Add new group $name")
        withContext(Dispatchers.IO) {
            universityDao.insertNewGroup(group)
            loadFacultyGroups(facultyID)
        }
    }

    suspend fun loadFacultyGroups(facultyId: Int) {
        Log.d("GET_GRP", "GET group list")
        withContext(Dispatchers.IO) {
            faculty.postValue(universityDao.loadFacultyGroups(facultyId))
        }
    }

    // *******************************************
    //              About Student
    // *******************************************

    suspend fun getGroupStudents(groupId: Int): List<Student> {
        var f: List<Student> = emptyList()
        val job = CoroutineScope(Dispatchers.IO).launch {
            f = universityDao.loadGroupStudents(groupId)
        }
        job.join()
        Log.d("GET_STD", "GET students list")
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
        Log.d(
            "ADD_STD",
            "${student.id} ${student.groupId} ${student.firstName} ${student.middleName} ${student.lastName} " +
                    "${student.phone} ${student.birthDate}"
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

    suspend fun deleteStudent(student: Student) {
        Log.d(
            "DEL_STD",
            "${student.id} ${student.groupId} ${student.firstName} ${student.middleName} ${student.lastName} " +
                    "${student.phone} ${student.birthDate}"
        )
        withContext(Dispatchers.IO) {
            student.id?.let { universityDao.deleteStudentByID(it) }
        }
    }
}

// *******************************************
//              Another request method
// *******************************************
//
//    fun getServerFaculty() {
//        if (myServerAPI == null) getAPI()
//        CoroutineScope(Dispatchers.Main).launch {
//            fetchFaculty()
//        }
//    }

//    private suspend fun fetchFaculty() {
//        if (myServerAPI != null) {
//            val job = CoroutineScope(Dispatchers.IO).launch {
//                val r = myServerAPI!!.getFaculty().awaitResponse()
//                if (r.isSuccessful) {
//                    val job = CoroutineScope(Dispatchers.IO).launch {
//                        universityDao.deleteAllFaculty()
//                    }
//                    job.join()
//                    val faculties = r.body()
//                    val facultyList = faculties?.items
//                    if (facultyList != null) {
//                        for (f in facultyList) {
//                            universityDao.insertNewFaculty(f)
//                        }
//                    }
//                }
//            }
//        }
//    }
// *******************************************