package com.example.second34_2.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.second34_2.data.Faculty
import com.example.second34_2.R
import java.util.*

const val FACULTY_TAG = "FacultyFragment"
const val FACULTY__TITLE = "Университет"

class FacultyFragment : Fragment() {
    private lateinit var rvFaculty: RecyclerView
    private lateinit var viewModel: FacultyViewModel
    private var adapter: FacultyListAdapter? = FacultyListAdapter(emptyList())

    companion object {
        fun newInstance() = FacultyFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faculty, container, false)
        rvFaculty = view.findViewById(R.id.rvFaculty)
        rvFaculty.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FacultyViewModel::class.java)
        viewModel.university.observe(viewLifecycleOwner) {
            adapter = FacultyListAdapter(it)
            rvFaculty.adapter = adapter
        }
        callbacks?.setTitle(FACULTY__TITLE)
    }

    private inner class FacultyHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        lateinit var faculty: Faculty

        fun bind(faculty: Faculty) {
            this.faculty = faculty
            itemView.findViewById<TextView>(R.id.tv_FacultyElement).text = faculty.name
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            callbacks?.showGroupFragment(faculty.id)
        }
    }

    private inner class FacultyListAdapter(private val items: List<Faculty>) :
        RecyclerView.Adapter<FacultyHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        )
                : FacultyHolder {
            val view = layoutInflater.inflate(R.layout.layout_faculty_listelement, parent, false)
            return FacultyHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: FacultyHolder, position: Int) {
            holder.bind(items[position])
        }
    }

    interface Callbacks {
        fun setTitle(_title: String)
        fun showGroupFragment(FacultyID: UUID)
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