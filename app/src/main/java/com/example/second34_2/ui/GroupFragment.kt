package com.example.second34_2.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.second34_2.data.Faculty
import com.example.second34_2.data.Student
import com.example.second34_2.databinding.FragmentGroupBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

const val GROUP_TAG = "GroupFragment"

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    companion object {
        private lateinit var _facultyID: UUID
        fun newInstance(facultyID: UUID): GroupFragment {
            _facultyID = facultyID
            return GroupFragment()
        }

        val getFacultyID get() = _facultyID
    }

    private lateinit var viewModel: FacultyGroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FacultyGroupViewModel::class.java)
        viewModel.setFaculty(_facultyID)
        viewModel.faculty.observe(viewLifecycleOwner) {
            updateUI(it)
            callbacks?.setTitle(it?.name ?: "")
        }
    }
    private var tabPosition: Int = 0

    private fun updateUI(Faculty: Faculty?) {
        binding.tabLayoutGroup.clearOnTabSelectedListeners()
        binding.tabLayoutGroup.removeAllTabs()
        for (i in 0 until (Faculty?.groups?.size ?: 0)) {
            binding.tabLayoutGroup.addTab(binding.tabLayoutGroup.newTab().apply {
                text = i.toString()
            })
        }

        binding.faBtnAddStudent.visibility=
            if((Faculty?.groups?.size ?:0)==0)
                View.GONE
            else {
                binding.faBtnAddStudent.setOnClickListener {
                    callbacks?.showStudent(Faculty!!.groups!!.get(tabPosition).id,null)
                }
                View.VISIBLE
            }
        val adapter = GroupPageAdapter(requireActivity(), Faculty!!)
        binding.viewPageGroups.adapter = adapter
        TabLayoutMediator(binding.tabLayoutGroup, binding.viewPageGroups, true, true) { tab, pos ->
            tab.text = Faculty?.groups?.get(pos)?.name
        }.attach()

        binding.tabLayoutGroup.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position!!
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }
        })
    }

    private inner class GroupPageAdapter(fa: FragmentActivity, private val faculty: Faculty) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return faculty.groups?.size ?: 0
        }

        override fun createFragment(position: Int): Fragment {
            return GroupList(faculty.groups?.get(position)!!)
        }
    }

    interface Callbacks {
        fun setTitle(_title: String)
        fun showStudent(groupID: UUID, student: Student?)
    }

    var callbacks: Callbacks? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        callbacks = null
        super.onDetach()
    }

}