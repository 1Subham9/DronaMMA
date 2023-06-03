package com.amtron.dronamma.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.amtron.dronamma.databinding.ActivityStudentBinding
import com.amtron.dronamma.helper.Common
import com.amtron.dronamma.model.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentBinding
    private lateinit var student: Student


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val bundle = intent.extras!!

        supportActionBar?.title = "Student Details"

        val id = bundle.getString("id").toString()


        Common.studentRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val data = dataSnapshot.value // Retrieve the data
                // Do something with the data

                student = dataSnapshot.getValue(Student::class.java)!!

                binding.name.text = student.name.toString()
                binding.gender.text = "Gender: ${student.gender}"
                binding.age.text = "Age: ${calculateAge(student.birthday.toString())}"
                binding.className.text = "Class: ${student.className}"
                binding.mobile.text = "Mobile: ${student.mobile}"
                binding.batch.text = "Mobile: ${student.batch}"
                binding.address.text = "Address:\n${student.address}"

                val type: String = if (student.paid == 0) "Regular" else "Subscription"

                binding.payment.text = "Payment Type: $type"


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
            }
        })


    }


    fun calculateAge(birthDate: String): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val date = dateFormat.parse(birthDate)
        calendar.time = date
        val birthYear = calendar.get(Calendar.YEAR)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return currentYear - birthYear
    }
}