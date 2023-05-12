package com.example.second34_2.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.second34_2.data.Faculty
import com.example.second34_2.data.Group
import kotlinx.coroutines.launch
import repository.FacultyRepository

class GroupListViewModel : ViewModel() {
    var faculty: MutableLiveData<List<Group>> = MutableLiveData()
    private var _facultyID: Int = -1

    fun setFaculty(facultyId: Int) {
        _facultyID = facultyId
        FacultyRepository.get().faculty.observeForever {
            faculty.postValue(it)
        }
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            FacultyRepository.get().loadFacultyGroups(_facultyID)
        }
    }

    private fun getFaculty(): Faculty? {
        var f: Faculty? = null
        val job = viewModelScope.launch {
            f = FacultyRepository.get().getFaculty(_facultyID)
        }
        return f
    }

    /*fun deleteStudent(groupID: UUID, student: Student)=
        FacultyRepository.get().deleteStudent(groupID,student)*/
}