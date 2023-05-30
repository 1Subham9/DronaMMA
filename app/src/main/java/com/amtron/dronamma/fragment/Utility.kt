package com.amtron.dronamma.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amtron.dronamma.databinding.FragmentUtilityBinding
import com.amtron.dronamma.model.Student
import com.amtron.dronamma.model.Attendance
import com.amtron.dronamma.model.Date
import com.amtron.dronamma.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar


class Utility : Fragment() {


    private lateinit var binding: FragmentUtilityBinding

    private lateinit var studentRef: DatabaseReference
    private lateinit var attendanceRef: DatabaseReference
    private lateinit var dateRef: DatabaseReference
    private lateinit var user : User
    private lateinit var branch : String

    private var storedDate : String = ""
    private var date: Date? = null

    private lateinit var studentList: ArrayList<Student>

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var currentDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUtilityBinding.inflate(inflater, container, false)

        val cal = Calendar.getInstance()

        val myYear = cal.get(Calendar.YEAR)
        val myMonth = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        studentList = arrayListOf()


        val setDate: String = if (day < 10) {
            "0$day"
        } else {
            "$day"
        }

        val setMonth: String = if (myMonth + 1 < 10) {
            "0${myMonth + 1}"
        } else {
            "${myMonth + 1}"
        }

        currentDate = "$myYear-$setMonth-$setDate"

        sharedPreferences =
            requireActivity().getSharedPreferences("Drona", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()


        user = Gson().fromJson(
            sharedPreferences.getString("user", "").toString(), object : TypeToken<User>() {}.type
        )

        branch = user.branch.toString()


        studentRef = FirebaseDatabase.getInstance().getReference("Students")
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance")
        dateRef = FirebaseDatabase.getInstance().getReference("Date")


        // populate student list
        studentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (emSnap in snapshot.children) {
                        val studentData = emSnap.getValue(Student::class.java)

                        if (studentData != null) {

                            studentList.add(studentData)

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "$error", Toast.LENGTH_SHORT).show()
            }
        })


//         Retrieve the value from the database
        dateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {


                    for (emSnap in snapshot.children) {
                        val dateData = emSnap.getValue(Date::class.java)

                        if (dateData != null && dateData.branch == branch) {

                            date = dateData

                            storedDate = date!!.date.toString()


                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
            }
        })


        binding.addStudentForTodayAttendance.setOnClickListener {


            if (storedDate.isEmpty() || storedDate != currentDate) {

                date!!.date= currentDate
                val id = date!!.id

                if (id != null) {
                    dateRef.child(id).setValue(date).addOnSuccessListener {

                        // Value successfully stored in the database
                        Log.d("FirebaseDatabase", "Variable value stored for this month")
                    }.addOnFailureListener { exception ->
                        // Error occurred while storing the value
                        Log.e("FirebaseDatabase", "Error storing value: $exception")
                    }
                }



                for (student : Student in studentList) {

                    val attendanceId = attendanceRef.push().key!!

                    val attendance = Attendance(
                        attendanceId,
                        student.name,
                        student.className,
                        student.id,
                        student.batch,
                        student.branch,
                        currentDate,
                        0
                    )

                    attendanceRef.child(attendanceId).setValue(attendance).addOnCompleteListener {

                        // Add Attendance list for current date

                        Toast.makeText(
                            requireActivity(),
                            "Attendance added for today's date",
                            Toast.LENGTH_SHORT
                        ).show()

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                    }





                }
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Attendance already added for today's date",
                    Toast.LENGTH_SHORT
                ).show()

            }


        }


        return binding.root
    }


}