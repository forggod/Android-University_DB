package com.example.second34_2.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.example.second34_2.data.Student
import com.example.second34_2.R
import com.example.second34_2.databinding.FragmentStudentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.FacultyRepository
import java.util.*

const val STUDENT_TAG = "StudentFragment"

class StudentFragment : Fragment() {

    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding!!

    companion object {
        private var student: Student? = null
        private var groupID: Int? = null
        fun newInstance(groupID: Int, student: Student?): StudentFragment {
            this.student = student
            this.groupID = groupID
            return StudentFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var selectedDate = GregorianCalendar()
    private lateinit var viewModel: StudentViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (student != null) {
            binding.editTextTextPersonNameFirstName.setText(student!!.firstName)
            binding.editTextTextPersonNameName.setText(student!!.middleName)
            binding.editTextTextPersonNameLastName.setText(student!!.lastName)
            binding.editTextPhone.setText(student!!.phone)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = student!!.birthDate!!
            binding.calendarID.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
        viewModel = ViewModelProvider(this).get(StudentViewModel::class.java)
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showCommitDialog()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun showCommitDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setCancelable(true)
        builder.setMessage("Сохранить изменения?")
        builder.setTitle("Подтверждение")
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            var p = true
            binding.editTextTextPersonNameFirstName.text.toString().ifBlank {
                p = false
                binding.editTextTextPersonNameFirstName.error = "Укажите значение"
            }
            binding.editTextTextPersonNameLastName.text.toString().ifBlank {
                p = false
                binding.editTextTextPersonNameLastName.error = "Укажите значение"
            }
            binding.editTextTextPersonNameName.text.toString().ifBlank {
                p = false
                binding.editTextTextPersonNameName.error = "Укажите значение"
            }
            binding.editTextPhone.text.toString().ifBlank {
                p = false
                binding.editTextPhone.error = "Укажите значение"
            }

            if (GregorianCalendar().get(GregorianCalendar.YEAR) - binding.calendarID.year < 10) {
                p = false
                Toast.makeText(context, "Укажите правильно возраст", Toast.LENGTH_LONG).show()
            }

            if (p) {
                val calendar = Calendar.getInstance()
                calendar.set(
                    binding.calendarID.year,
                    binding.calendarID.month,
                    binding.calendarID.dayOfMonth
                )
                val firstName = binding.editTextTextPersonNameFirstName.text.toString()
                val lastName = binding.editTextTextPersonNameLastName.text.toString()
                val middleName = binding.editTextTextPersonNameName.text.toString()
                val phone = binding.editTextPhone.text.toString()
                val date = calendar.timeInMillis
                Log.d(
                    "STD", "DATA id: ${student?.id} groupId: ${student?.groupId} " +
                            "firstName: $firstName middleName: $middleName " +
                            "lastName: $lastName phone: $phone " +
                            "date: $date"
                )
                if (student == null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        FacultyRepository.get()
                            .newStudent(groupID!!, firstName, lastName, middleName, phone, date)
                    }
                }
                if (student != null) {
                    val studentNew = Student(
                        id = student!!.id,
                        groupId = student!!.groupId,
                        firstName = firstName,
                        lastName = lastName,
                        middleName = middleName,
                        phone = phone,
                        birthDate = date
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        FacultyRepository.get()
                            .editStudent(studentNew)
                    }
                }
                backPressedCallback.isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        builder.setNegativeButton("Отмена") { _, _ ->
            backPressedCallback.isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val alert = builder.create()
        alert.show()
    }
}