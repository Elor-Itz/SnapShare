package com.example.snapshare.model

class Model private constructor() {

    // Singleton pattern for Model
    companion object {
        val instance: Model by lazy { Model() }
    }

    // List to hold all the students
    val data: MutableList<Student> = mutableListOf()

    init {
        // Prepopulate with some sample students
        for (i in 1..20) {
            addStudent(Student("student $i", "12345678", "", "0541234567", "Tel Aviv", false))
        }
    }

    // Returns all students
    fun getAllStudents(): MutableList<Student> = data

    // Adds a student to the list
    fun addStudent(student: Student) {
        data.add(student)
    }
}