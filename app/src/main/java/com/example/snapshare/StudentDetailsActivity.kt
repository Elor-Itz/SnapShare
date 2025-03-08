package com.example.snapshare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.snapshare.model.Model
import com.example.snapshare.model.Student

class StudentDetailsActivity : AppCompatActivity() {

    private val repository = Model.instance
    private var position: Int = -1

    // Launcher for EditStudentActivity
    private val editStudentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val studentPosition = result.data?.getIntExtra("STUDENT_POSITION", -1)
            val deleted = result.data?.getBooleanExtra("DELETED", false) ?: false

            if (deleted && studentPosition != null && studentPosition != -1) {
                // Remove the student from the repository
                if (studentPosition in repository.getAllStudents().indices) {
                    repository.getAllStudents().removeAt(studentPosition)

                    // Notify RecyclerView of the removal
                    Toast.makeText(this, "Student deleted successfully!", Toast.LENGTH_SHORT).show()

                    // Returning to the previous activity (RecyclerView)
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Error: Invalid position for deletion!", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle the update case
                val updatedStudent = result.data?.getParcelableExtra<Student>("UPDATED_STUDENT")
                if (updatedStudent != null && studentPosition != null && studentPosition != -1) {
                    repository.getAllStudents()[studentPosition] = updatedStudent
                    displayStudentDetails(updatedStudent)
                    Toast.makeText(this, "Details updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update details!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        val nameTextView: TextView = findViewById(R.id.studentDetailsName)
        val idTextView: TextView = findViewById(R.id.studentDetailsId)
        val phoneTextView: TextView = findViewById(R.id.studentDetailsPhone)
        val addressTextView: TextView = findViewById(R.id.studentDetailsAddress)
        val editButton: Button = findViewById(R.id.editStudentButton)
        val deleteButton: Button = findViewById(R.id.deleteStudentButton)

        // Retrieve the student position
        position = intent.getIntExtra("STUDENT_POSITION", -1)

        if (position in repository.getAllStudents().indices) {
            val student = repository.getAllStudents()[position]
            displayStudentDetails(student)

            editButton.setOnClickListener {
                // Launch EditStudentActivity
                val intent = Intent(this, EditStudentActivity::class.java).apply {
                    putExtra("STUDENT_POSITION", position)
                }
                editStudentLauncher.launch(intent)
            }

            deleteButton.setOnClickListener {
                // Handle deletion
                if (position != -1) {
                    repository.getAllStudents().removeAt(position)
                    Toast.makeText(this, "Student deleted successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // Notify RecyclerView to update
                    finish() // Go back to the previous activity
                }
            }
        } else {
            Toast.makeText(this, "Invalid student position!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayStudentDetails(student: Student) {
        findViewById<TextView>(R.id.studentDetailsName).text = student.name
        findViewById<TextView>(R.id.studentDetailsId).text = student.id
        findViewById<TextView>(R.id.studentDetailsPhone).text = student.phone
        findViewById<TextView>(R.id.studentDetailsAddress).text = student.address
    }
}