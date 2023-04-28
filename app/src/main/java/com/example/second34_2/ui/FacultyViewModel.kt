package com.example.second34_2.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.second34_2.data.Faculty
import repository.FacultyRepository

class FacultyViewModel : ViewModel() {
    var university: MutableLiveData<List<Faculty>> = MutableLiveData()
    init{
        FacultyRepository.get().university.observeForever{
            university.postValue(it)
        }
    }
    fun newFaculty(name:String)=
        FacultyRepository.get().newFaculty(name)
}