package com.example.second34_2.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.second34_2.data.Faculty
import repository.FacultyRepository
import java.util.UUID

class FacultyGroupViewModel : ViewModel() {
    var faculty: MutableLiveData<Faculty?> = MutableLiveData()
    private lateinit var _facultyID: UUID
    fun setFaculty(facultyID: UUID) {
        _facultyID = facultyID
        FacultyRepository.get().university.observeForever {
            faculty.postValue(it.find { it.id == _facultyID })
        }
    }
}