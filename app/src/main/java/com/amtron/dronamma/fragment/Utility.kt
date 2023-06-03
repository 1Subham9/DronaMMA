package com.amtron.dronamma.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.amtron.dronamma.databinding.FragmentUtilityBinding
import com.amtron.dronamma.helper.Common.Companion.attendanceList
import com.amtron.dronamma.helper.Common.Companion.attendanceRef
import com.amtron.dronamma.helper.Common.Companion.branch
import com.amtron.dronamma.helper.Common.Companion.studentList
import com.amtron.dronamma.helper.Common.Companion.studentRef
import com.amtron.dronamma.model.Attendance
import com.amtron.dronamma.model.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar


class Utility : Fragment() {


    private lateinit var binding: FragmentUtilityBinding


    private var flag: Boolean = true


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






        for (attendanceData: Attendance in attendanceList) {
            if (attendanceData.date.toString() == currentDate && attendanceData.branch == branch) {
                flag = false
                break
            }
        }


        // populate student list
        studentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    studentList.clear()

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


            if (flag) {


                for (student: Student in studentList) {

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

                        Log.d("Firebase", "Attendance added for today's date")

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