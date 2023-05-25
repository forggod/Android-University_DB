package com.example.second34_2.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.second34_2.data.Faculty
import com.example.second34_2.data.Group
import com.example.second34_2.data.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.FacultyRepository
import java.util.UUID

class GroupListViewModel : ViewModel() {
    suspend fun deleteStudent(student: Student) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            FacultyRepository.get().universityDao.deleteStudent(student)
        }
        job.join()
    }
}