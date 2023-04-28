package com.example.second34_2.ui

import androidx.lifecycle.ViewModel
import com.example.second34_2.data.Student
import repository.FacultyRepository
import java.util.*

class GroupListViewModel: ViewModel() {
    fun deleteStudent(groupID: UUID, student: Student)=
        FacultyRepository.get().deleteStudent(groupID,student)
}