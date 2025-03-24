package com.example.snapshare

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.snapshare.model.Model
import com.example.snapshare.model.Student

class NewStudentActivity : AppCompatActivity() {

    private val repository = Model.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_student)

        val nameEditText: EditText = findViewById(R.id.newStudentName)
        val idEditText: EditText = findViewById(R.id.newStudentId)
        val phoneEditText: EditText = findViewById(R.id.newStudentPhone)
        val addressEditText: EditText = findViewById(R.id.newStudentAddress)
        val addButton: Button = findViewById(R.id.addStudentButton)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val id = idEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val address = addressEditText.text.toString()

            // Add new student to the repository
            if (name.isNotEmpty() && id.isNotEmpty()) {
                repository.addStudent(
                    Student(
                        name = name,
                        id = id,
                        imageUrl = "", // Default placeholder for image
                        phone = phone,
                        address = address,
                        cb = false
                    )
                )
                finish() // Close the activity
            }
        }
    }
}