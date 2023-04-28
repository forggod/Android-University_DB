package com.example.second34_2.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.second34_2.data.Group
import com.example.second34_2.data.Student
import com.example.second34_2.R
import com.example.second34_2.databinding.FragmentGroupListBinding
import java.util.*


class GroupList(private val group: Group) : Fragment() {
    private var _binding: FragmentGroupListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel:GroupListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupListBinding.inflate(inflater, container, false)
        binding.recycleViewGroupList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleViewGroupList.adapter = GroupListAdapter(group?.student ?: emptyList())
    }

    private inner class GroupHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        lateinit var student: Student
        fun bind(student: Student) {
            this.student = student
            val s = "${student.lastname}. ${student.firstname.get(0)}. ${student.midlename.get(0)}."
            itemView.findViewById<TextView>(R.id.tvElement).text = s
            itemView.findViewById<ConstraintLayout>(R.id.clButtons).visibility = View.GONE
            itemView.findViewById<ImageButton>(R.id.ibDelete).setOnClickListener {
                showDeleteDialog(student)
            }
            itemView.findViewById<ImageButton>(R.id.ibEdit).setOnClickListener {
                callbacks?.showStudent(group.id,student)
            }
        }



        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val cl = itemView.findViewById<ConstraintLayout>(R.id.clButtons)
            cl.visibility = View.VISIBLE
            lastItemView?.findViewById<ConstraintLayout>(R.id.clButtons)?.visibility=View.GONE
            lastItemView = if (lastItemView==itemView) null else itemView
        }
    }

    private var lastItemView: View? = null

    private fun showDeleteDialog(student: Student){
        val builder=AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить студента ${student.lastname} ${student.firstname} ${student.midlename} из списка?")
        builder.setTitle("Подтверждение")
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            viewModel.deleteStudent(group.id,student)
        }
        builder.setNegativeButton("Отмена",null)
        val alert=builder.create()
        alert.show()
    }

    private inner class GroupListAdapter(private val items: List<Student>) :
        RecyclerView.Adapter<GroupHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
            val view = layoutInflater.inflate(R.layout.layout_student_listelement, parent, false)

            return GroupHolder(view)
        }

        override fun getItemCount(): Int = items.size
        override fun onBindViewHolder(holder: GroupHolder, position: Int) {
            holder.bind(items[position])
        }
    }
    interface Callbacks {
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