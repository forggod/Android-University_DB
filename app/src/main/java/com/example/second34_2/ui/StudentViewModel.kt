package com.example.second34_2.ui

import androidx.lifecycle.ViewModel
import com.example.second34_2.data.Student
import repository.FacultyRepository
import java.util.UUID

class StudentViewModel : ViewModel() {

    fun newStudent(groupID: UUID, student: Student) =
        FacultyRepository.get().newStudent(groupID, student)

    fun editStudent(groupID: UUID,student: Student)=
        FacultyRepository.get().editStudent(groupID,student)
}