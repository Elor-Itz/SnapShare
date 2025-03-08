package com.example.snapshare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapshare.model.Model
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentsRecyclerViewActivity : AppCompatActivity(), StudentAdapter.OnStudentClickListener {

    private val repository = Model.instance  // Accessing the singleton Model
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_recycler_view)

        // Initialize RecyclerView and FloatingActionButton
        val recyclerView: RecyclerView = findViewById(R.id.studentRecyclerView)
        val addButton: FloatingActionButton = findViewById(R.id.addStudentButton)

        // Set up RecyclerView with LinearLayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter(repository.getAllStudents(), this)
        recyclerView.adapter = adapter

        // Handle Add Button click
        addButton.setOnClickListener {
            startActivity(Intent(this, NewStudentActivity::class.java))
        }
    }

    override fun onStudentClick(position: Int) {
        val intent = Intent(this, StudentDetailsActivity::class.java)
        intent.putExtra("STUDENT_POSITION", position)
        startActivity(intent)
    }

    override fun onCheckChanged(position: Int, isChecked: Boolean) {
        val students = repository.getAllStudents()

        // Ensure position is valid
        if (position in students.indices) {
            val student = students[position]
            student.cb = isChecked
            adapter.notifyItemChanged(position)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}