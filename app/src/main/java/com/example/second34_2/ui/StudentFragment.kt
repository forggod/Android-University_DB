package com.example.second34_2.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
import java.util.*

const val STUDENT_TAG = "StudentFragment"

class StudentFragment : Fragment() {

    private var _binding: FragmentStudentBinding? = null
    private val binding get() = _binding!!

    companion object {
        private var student: Student? = null
        private var groupID: UUID? = null
        fun newInstance(groupID: UUID, student: Student?): StudentFragment {
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

    //   private var selectedDate = GregorianCalendar()
    private lateinit var viewModel: StudentViewModel

  /*  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (student != null) {
            binding.editTextTextPersonNameFirstName.setText(student!!.firstname)
            binding.editTextTextPersonNameName.setText(student!!.midlename)
            binding.editTextTextPersonNameLastName.setText(student!!.lastname)
            binding.editTextPhone.setText(student!!.phonenumber)
            val dt = GregorianCalendar().apply {
                time = student!!.birthdate

            }
            binding.calendarID.init(
                dt.get(Calendar.YEAR), dt.get(Calendar.MONTH),
                dt.get(Calendar.DAY_OF_MONTH), null
            )
        }
        viewModel = ViewModelProvider(this).get(StudentViewModel::class.java)
        /* binding.calendarViewBirthDate.setOnDateChangeListener{_ ,year, month, dayOfMonth ->
             selectedDate.apply {
                 set(GregorianCalendar.YEAR,year)
                 set(GregorianCalendar.MONTH,month)
                 set(GregorianCalendar.DAY_OF_MONTH,dayOfMonth)
             }
         }*/
    }*/

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
                val selectedDate = GregorianCalendar().apply {
                    set(GregorianCalendar.YEAR, binding.calendarID.year)
                    set(GregorianCalendar.YEAR, binding.calendarID.month)
                    set(GregorianCalendar.YEAR, binding.calendarID.dayOfMonth)
                }
                if (student == null) {
                    student?.apply {
                     /*   firstname = binding.editTextTextPersonNameFirstName.text.toString()
                        lastname = binding.editTextTextPersonNameLastName.text.toString()
                        midlename = binding.editTextTextPersonNameName.text.toString()
                        phonenumber = binding.editTextPhone.text.toString()
                        birthdate = Date(selectedDate.time.time)

                      */
                    }
                  //  viewModel.newStudent(groupID!!, student!!)
                } else {
                    student?.apply {
                      /*  firstname = binding.editTextTextPersonNameFirstName.text.toString()
                        lastname = binding.editTextTextPersonNameLastName.text.toString()
                        midlename = binding.editTextTextPersonNameName.text.toString()
                        phonenumber = binding.editTextPhone.text.toString()
                        birthdate = Date(selectedDate.time.time) */
                    }
                  //  viewModel.editStudent(groupID!!, student!!)
                }
                backPressedCallback.isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            val alert = builder.create()
            alert.show()
        }
    }
}