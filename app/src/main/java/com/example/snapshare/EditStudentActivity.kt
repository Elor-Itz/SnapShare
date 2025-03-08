package com.example.snapshare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snapshare.model.Model

class EditStudentActivity : AppCompatActivity() {

    private val repository = Model.instance
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        val nameEditText: EditText = findViewById(R.id.editStudentName)
        val idEditText: EditText = findViewById(R.id.editStudentId)
        val phoneEditText: EditText = findViewById(R.id.editStudentPhone)
        val addressEditText: EditText = findViewById(R.id.editStudentAddress)
        val saveButton: Button = findViewById(R.id.saveStudentButton)

        // Retrieve the student position from the intent
        position = intent.getIntExtra("STUDENT_POSITION", -1)

        if (position != -1) {
            val student = repository.getAllStudents()[position]

            // Populate the fields with the current student data
            nameEditText.setText(student.name)
            idEditText.setText(student.id)
            phoneEditText.setText(student.phone)
            addressEditText.setText(student.address)

            // Save button updates the student details
            saveButton.setOnClickListener {
                val updatedName = nameEditText.text.toString()
                val updatedId = idEditText.text.toString()
                val updatedPhone = phoneEditText.text.toString()
                val updatedAddress = addressEditText.text.toString()

                // Validate input before updating
                if (updatedName.isNotBlank() && updatedId.isNotBlank() &&
                    updatedPhone.isNotBlank() && updatedAddress.isNotBlank()
                ) {

                    // Update the student object
                    student.apply {
                        name = updatedName
                        id = updatedId
                        phone = updatedPhone
                        address = updatedAddress
                    }

                    // Pass updated student back to StudentDetailsActivity
                    val resultIntent = Intent().apply {
                        putExtra("UPDATED_STUDENT", student)  // Pass Parcelable Student
                        putExtra("STUDENT_POSITION", position)  // Pass position
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    Toast.makeText(this, "Student details updated!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}