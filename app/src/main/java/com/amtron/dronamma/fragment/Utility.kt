package com.amtron.dronamma.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amtron.dronamma.databinding.FragmentUtilityBinding
import com.amtron.dronamma.model.Student
import com.amtron.dronamma.model.Attendance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar


class Utility : Fragment() {


    private lateinit var binding: FragmentUtilityBinding

    private lateinit var studentRef: DatabaseReference
    private lateinit var attendanceRef: DatabaseReference

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


        studentRef = FirebaseDatabase.getInstance().getReference("Students")
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance")


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







        binding.addStudentForTodayAttendance.setOnClickListener {


            val storedDate = sharedPreferences.getString("date", "").toString()


            if (storedDate.isEmpty() || storedDate != currentDate) {
                editor.putString("date", currentDate)
                editor.apply()


                for (student : Student in studentList) {

                    val attendanceId = attendanceRef.push().key!!

                    val attendance = Attendance(
                        attendanceId,
                        student.name,
                        student.className,
                        student.id,
                        student.batch,
                        currentDate,
                        0
                    )

                    attendanceRef.child(attendanceId).setValue(attendance).addOnCompleteListener {
//                        Toast.makeText(
//                            requireContext(), "Data inserted successfully", Toast.LENGTH_SHORT
//                        ).show()

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                    }


                    // Add Attendance list for current date

                    Toast.makeText(
                        requireActivity(),
                        "Attendance added for today's date",
                        Toast.LENGTH_SHORT
                    ).show()
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