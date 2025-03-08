package com.example.snapshare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapshare.model.Student

class StudentAdapter(
    private val students: List<Student>,
    private val listener: OnStudentClickListener
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    interface OnStudentClickListener {
        fun onStudentClick(position: Int)
        fun onCheckChanged(position: Int, isChecked: Boolean)
    }

    inner class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.studentName)
        val idTextView: TextView = view.findViewById(R.id.studentId)
        val phoneTextView: TextView = view.findViewById(R.id.studentPhone)
        val checkbox: CheckBox = view.findViewById(R.id.studentCheckBox)

        init {
            // Click listener for the entire item
            view.setOnClickListener {
                listener.onStudentClick(adapterPosition)
            }

            // Checkbox change listener
            checkbox.setOnClickListener {
                val isChecked = checkbox.isChecked
                listener.onCheckChanged(adapterPosition, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_item, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]

        // Bind data to views
        holder.nameTextView.text = student.name
        holder.idTextView.text = student.id
        holder.phoneTextView.text = student.phone

        // Avoid triggering listener during binding
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = student.cb

        // Reattach listener after state update
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            listener.onCheckChanged(holder.adapterPosition, isChecked)
        }
    }

    override fun getItemCount(): Int = students.size
}