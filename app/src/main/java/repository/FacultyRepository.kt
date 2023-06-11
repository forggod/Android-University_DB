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
import kotlinx.coroutines.delay
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
        val IP = "192.168.0.107"
        val PORT = 5050
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
        try {
            val url = "$IP:$PORT"
            Retrofit.Builder()
                .baseUrl("http://${url}/university/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().apply {
                    myServerAPI = create(ServerAPI::class.java)
                }
        } catch (e: Exception) {
            Log.e("EX", "Возникло исключение $e \n http://$IP:$PORT/university/")
        }
    }

    fun syncUniversity() {
        getUniversity()
    }

    fun syncPost() {
        postFaculties()
        postGroups()
        postStudents()
    }

    suspend fun loadUniversity() {
        Log.d("GET_FAC", "GET list of faculties")
        withContext(Dispatchers.IO) {
            university.postValue(universityDao.loadFaculty())
        }
        withContext(Dispatchers.IO) {
            faculty.postValue(universityDao.loadGroup())
        }
    }

    private fun getUniversity() {
        if (myServerAPI == null)
            getAPI()
        if (myServerAPI != null) {
            val requestFaculty = myServerAPI!!.getFaculty()
            requestFaculty.enqueue(object : Callback<List<Faculty>> {
                override fun onFailure(call: Call<List<Faculty>>, t: Throwable) {
                    Log.e(TAG, "Ошибка получения истории факультета", t)
                }

                override fun onResponse(
                    call: Call<List<Faculty>>,
                    response: Response<List<Faculty>>
                ) {
                    Log.d(TAG, "Получение истории факультета")
                    val facultiesList = response.body()
                    CoroutineScope(Dispatchers.IO).launch {
                        universityDao.deleteAllFaculty()
                        if (facultiesList != null) {
                            for (f in facultiesList) {
                                universityDao.insertNewFaculty(f)
                            }
                        }
                        loadUniversity()
                    }
                }
            })
            val requestGroups = myServerAPI!!.getGroups()
            requestGroups.enqueue(object : Callback<List<Group>> {
                override fun onFailure(call: Call<List<Group>>, t: Throwable) {
                    Log.e(TAG, "Ошибка получения истории групп", t)
                }

                override fun onResponse(
                    call: Call<List<Group>>,
                    response: Response<List<Group>>
                ) {
                    Log.d(TAG, "Получение истории групп")
                    val groupsList = response.body()
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(500)
                        universityDao.deleteAllGroups()
                        if (groupsList != null) {
                            for (f in groupsList) {
                                universityDao.insertNewGroup(f)
                            }
                        }
                        loadUniversity()
                    }
                }
            })
            val requestStudents = myServerAPI!!.getStudents()
            requestStudents.enqueue(object : Callback<List<Student>> {
                override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                    Log.e(TAG, "Ошибка получения истории студентов", t)
                }

                override fun onResponse(
                    call: Call<List<Student>>,
                    response: Response<List<Student>>
                ) {
                    Log.d(TAG, "Получение истории студентов")
                    val studentsList = response.body()
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(1000)
                        universityDao.deleteAllStudents()
                        if (studentsList != null) {
                            for (f in studentsList) {
                                universityDao.insertNewStudent(f)
                            }
                        }
                        loadUniversity()
                    }
                }
            })
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

    suspend fun getUniversity(facultyId: Int): Faculty? {
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

    private fun postFaculties() {
        if (myServerAPI == null)
            getAPI()
        if (myServerAPI != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val request = myServerAPI!!.postFaculty(universityDao.loadFaculty())
                request.enqueue(object : Callback<Faculties> {

                    override fun onFailure(call: Call<Faculties>, t: Throwable) {
                        Log.e(TAG, "Ошибка отправки истории факультетов", t)
                    }

                    override fun onResponse(
                        call: Call<Faculties>,
                        response: Response<Faculties>
                    ) {
                        Log.d(TAG, "Отправка истории факультетов")
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

    private fun postGroups() {
        if (myServerAPI == null)
            getAPI()
        if (myServerAPI != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val request = myServerAPI!!.postGroups(universityDao.loadGroup())
                request.enqueue(object : Callback<Groups> {

                    override fun onFailure(call: Call<Groups>, t: Throwable) {
                        Log.e(TAG, "Ошибка отправки истории групп", t)
                    }

                    override fun onResponse(
                        call: Call<Groups>,
                        response: Response<Groups>
                    ) {
                        Log.d(TAG, "Отправка истории групп")
                    }
                })
            }
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

    private fun postStudents() {
        if (myServerAPI == null)
            getAPI()
        if (myServerAPI != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val request = myServerAPI!!.postStudents(universityDao.loadStudent())
                request.enqueue(object : Callback<Students> {

                    override fun onFailure(call: Call<Students>, t: Throwable) {
                        Log.e(TAG, "Ошибка отправки истории студентов", t)
                    }

                    override fun onResponse(
                        call: Call<Students>,
                        response: Response<Students>
                    ) {
                        Log.d(TAG, "Отправка истории студентов")
                    }
                })
            }
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