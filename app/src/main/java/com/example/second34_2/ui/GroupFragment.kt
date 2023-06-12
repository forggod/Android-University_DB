package com.example.second34_2.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.size
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.second34_2.R
import com.example.second34_2.data.Faculty
import com.example.second34_2.data.Group
import com.example.second34_2.data.Student
import com.example.second34_2.databinding.FragmentGroupBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.FacultyRepository
import java.util.*

const val GROUP_TAG = "GroupFragment"

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    companion object {
        private var _facultyID: Int = -1
        fun newInstance(facultyID: Int): GroupFragment {
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
            CoroutineScope(Dispatchers.Main).launch {
                val f = viewModel.getFaculty()
                callbacks?.setTitle(f?.name ?: "UNKNOWN")
            }
        }
    }

    private var tabPosition: Int = 0

    private fun showDeleteGroup() {
        val name =
            binding.tabLayoutGroup.getTabAt(binding.tabLayoutGroup.selectedTabPosition)?.text.toString()
        val groups = FacultyRepository.get().faculty.value
        val group = groups?.find { it.name == name }
        if (group != null) {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setCancelable(true)
            builder.setMessage("Удалить группу?")
            builder.setTitle("Подтверждение")
            builder.setPositiveButton("Удалить") { _, _ ->
                Log.d("INFO", "Deleted group $group")
                CoroutineScope(Dispatchers.Main).launch {
                    group.let { FacultyRepository.get().deleteGroup(it) }
                }
            }
            builder.setNegativeButton(getString(R.string.cancel), null)
            val alert = builder.create()
            alert.show()
        }
    }

    private fun updateUI(groups: List<Group>) {
        binding.tabLayoutGroup.clearOnTabSelectedListeners()
        binding.tabLayoutGroup.removeAllTabs()

        for (i in 0 until (groups?.size ?: 0)) {
            binding.tabLayoutGroup.addTab(binding.tabLayoutGroup.newTab().apply {
                text = i.toString()
            })
        }

        binding.faBtnAddStudent.visibility =
            if ((groups?.size ?: 0) == 0)
                View.GONE
            else {
                binding.faBtnAddStudent.setOnClickListener {
                    groups!!.get(tabPosition).id?.let { it1 -> callbacks?.showStudent(it1, null) }
                }
                View.VISIBLE
            }
        binding.faBtnDeleteGroup.visibility =
            if ((groups?.size) == 0)
                View.GONE
            else {
                binding.faBtnDeleteGroup.setOnClickListener {
                    showDeleteGroup()
                }
                View.VISIBLE
            }
        val adapter = GroupPageAdapter(requireActivity(), groups)
        binding.viewPageGroups.adapter = adapter
        TabLayoutMediator(binding.tabLayoutGroup, binding.viewPageGroups, true, true) { tab, pos ->
            tab.text = groups?.get(pos)?.name
        }.attach()

        if (tabPosition < binding.tabLayoutGroup.tabCount) {
            binding.tabLayoutGroup.setScrollPosition(tabPosition, 0f, true)
            binding.viewPageGroups.currentItem = tabPosition
        }

        binding.tabLayoutGroup.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position!!
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private inner class GroupPageAdapter(fa: FragmentActivity, private val groups: List<Group>) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return groups?.size ?: 0
        }

        override fun createFragment(position: Int): Fragment {
            return GroupList(groups?.get(position)!!)
        }
    }

    interface Callbacks {
        fun setTitle(_title: String)
        fun showStudent(groupID: Int, student: Student?)
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