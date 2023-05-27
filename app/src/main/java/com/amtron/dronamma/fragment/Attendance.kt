package com.amtron.dronamma.fragment

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amtron.dronamma.adapter.AttendanceAdapter
import com.amtron.dronamma.adapter.BatchAndClassAdapter
import com.amtron.dronamma.databinding.FragmentAddStudentBinding
import com.amtron.dronamma.databinding.FragmentAttendanceBinding
import com.amtron.dronamma.model.Attendance
import com.amtron.dronamma.model.BatchClassModel
import com.amtron.dronamma.model.Payment
import com.amtron.dronamma.model.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar


class Attendance : Fragment(), AttendanceAdapter.ItemClickInterface {

    private lateinit var binding: FragmentAttendanceBinding
    private lateinit var attendanceRef: DatabaseReference
    private lateinit var attendanceList: ArrayList<Attendance>
    private lateinit var attendanceListDated: ArrayList<Attendance>
    private lateinit var attendanceAdapter: AttendanceAdapter
    private lateinit var selectDate: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAttendanceBinding.inflate(inflater, container, false)

        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance")

        resumeFunction()

        // populating adapter
        binding.attendanceRecycler.layoutManager = LinearLayoutManager(requireActivity())
        attendanceAdapter = AttendanceAdapter(this)
        binding.attendanceRecycler.adapter = attendanceAdapter


        // Current Date

        val cal = Calendar.getInstance()

        val myYear = cal.get(Calendar.YEAR)
        val myMonth = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        binding.selectDate.text = "Date: $day/${myMonth + 1}/$myYear"


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

        selectDate = "$myYear-$setMonth-$setDate"


        binding.searchStudentName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // We we add something later

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                attendanceListDated.clear()

                for (attendance: Attendance in attendanceList) {

                    val list: String? = attendance.name?.lowercase()?.replace(" ", "")
                    val input: String = s.toString().lowercase().replace(" ", "")

                    if (input.let { list?.contains(it) } == true && attendance.date==selectDate) {
                        attendanceListDated.add(attendance)
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {

                // We we add something later
                attendanceAdapter.updateList(attendanceListDated)

            }

        })








        return binding.root
    }


    override fun onResume() {
        super.onResume()


        // Get Data from firebase
        attendanceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    attendanceList = arrayListOf()
                    attendanceListDated = arrayListOf()

                    for (emSnap in snapshot.children) {
                        val attendanceData = emSnap.getValue(Attendance::class.java)

                        if (attendanceData != null) {
                            attendanceList.add(attendanceData)
                        }
                    }


                    for (attendance: Attendance in attendanceList) {
                        if (attendance.date == selectDate) {
                            attendanceListDated.add(attendance)
                        }
                    }

                    attendanceAdapter.updateList(attendanceListDated)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "$error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCheckDetails(id: String) {
        Toast.makeText(requireContext(), "Check Details Typed", Toast.LENGTH_SHORT).show()
    }

    override fun setCheckBoxTrue(attendance: Attendance) {

        attendance.present = 1

        attendanceRef.child(attendance.id.toString()).setValue(attendance).addOnCompleteListener {
//            Toast.makeText(
//                requireContext(), "${attendance.name} is set to Present", Toast.LENGTH_SHORT
//            ).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setCheckBoxFalse(attendance: Attendance) {
        attendance.present = 0

        attendanceRef.child(attendance.id.toString()).setValue(attendance).addOnCompleteListener {
//            Toast.makeText(
//                requireContext(), "${attendance.name} is set to Absent", Toast.LENGTH_SHORT
//            ).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resumeFunction() {
        val cal = Calendar.getInstance()

        val myYear = cal.get(Calendar.YEAR)
        val myMonth = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)


        var setDate: String = if (day < 10) {
            "0$day"
        } else {
            "$day"
        }

        var setMonth: String = if (myMonth + 1 < 10) {
            "0${myMonth + 1}"
        } else {
            "${myMonth + 1}"
        }

        selectDate = "$myYear-$setMonth-$setDate"


        binding.selectDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    attendanceListDated = arrayListOf()


                    binding.selectDate.text = "Date: $dayOfMonth/${month + 1}/$year"

                    setDate = if (dayOfMonth < 10) {
                        "0$dayOfMonth"
                    } else {
                        "$dayOfMonth"
                    }

                    setMonth = if (month + 1 < 10) {
                        "0${month + 1}"
                    } else {
                        "${month + 1}"
                    }

                    binding.selectDate.setTextColor(Color.parseColor("#000000"))

                    selectDate = "$year-$setMonth-$setDate"

                    for (attendance: Attendance in attendanceList) {
                        if (attendance.date == selectDate) {
                            attendanceListDated.add(attendance)
                        }
                    }

                    attendanceAdapter.updateList(attendanceListDated)


                },
                myYear,
                myMonth,
                day
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }


}