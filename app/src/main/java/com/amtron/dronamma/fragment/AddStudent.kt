package com.amtron.dronamma.fragment

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.amtron.dronamma.databinding.FragmentAddStudentBinding
import com.amtron.dronamma.model.Attendance
import com.amtron.dronamma.model.BatchClassModel
import com.amtron.dronamma.model.Payment
import com.amtron.dronamma.model.Student
import com.amtron.dronamma.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import kotlin.properties.Delegates


class AddStudent : Fragment() {

    private lateinit var binding: FragmentAddStudentBinding

    private val genderValue = mutableListOf<String>()
    private val monthValue = mutableListOf<String>()

    private var fees = 0.0
    private var month = 0

    private lateinit var birthDate: String

    private var gender = ""
    private lateinit var className: String
    private lateinit var batch: String
    private lateinit var branch: String

    private lateinit var paymentRef: DatabaseReference
    private lateinit var studentRef: DatabaseReference
    private lateinit var batchClassRef: DatabaseReference
    private lateinit var attendanceRef: DatabaseReference


    private lateinit var currentDate: String

    private var isAdvance by Delegates.notNull<Int>()


    private lateinit var batchNameList: ArrayList<String>
    private lateinit var classNameList: ArrayList<String>

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    lateinit var user: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddStudentBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireActivity().getSharedPreferences("Drona", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        user = Gson().fromJson(
            sharedPreferences.getString("user", "").toString(), object : TypeToken<User>() {}.type
        )

        paymentRef = FirebaseDatabase.getInstance().getReference("Payment")
        studentRef = FirebaseDatabase.getInstance().getReference("Students")
        batchClassRef = FirebaseDatabase.getInstance().getReference("BatchClass")
        attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance")

        isAdvance = 0


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


        className = ""
        batch = ""
        branch = user.branch.toString()

        setSpinners()
        resumeFunction()


        batchClassRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    batchNameList = arrayListOf()
                    classNameList = arrayListOf()

                    for (emSnap in snapshot.children) {
                        val batchClassData = emSnap.getValue(BatchClassModel::class.java)

                        if (batchClassData != null) {

                            if (batchClassData.batch == 1) {
                                batchNameList.add(batchClassData.batchOrClass.toString())
                            } else {
                                classNameList.add(batchClassData.batchOrClass.toString())
                            }
                        }

                    }

                    // populate spinner here


                    val batchAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        batchNameList
                    )

                    binding.selectBatch.setAdapter(batchAdapter)


                    val classAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        classNameList
                    )

                    binding.selectClass.setAdapter(classAdapter)



                    binding.selectBatch.setOnItemClickListener { parent, view, position, id ->

                        batch = batchNameList[position]
                    }

                    binding.selectClass.setOnItemClickListener { parent, view, position, id ->
                        className = classNameList[position]

                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "$error", Toast.LENGTH_SHORT).show()
            }
        })


        binding.submitStudent.setOnClickListener {

            if (binding.studentName.text!!.isEmpty()) {
                binding.studentName.error = "Please enter student's name"
            }

            if (binding.studentAddress.text!!.isEmpty()) {
                binding.studentAddress.error = "Please enter student's address"
            }

            if (binding.mobileNumber.text!!.isEmpty()) {
                binding.mobileNumber.error = "Please enter student's number"
            }

            if (binding.feesAmount.text!!.isEmpty()) {
                binding.feesAmount.error = "Please enter student's fees"
            }


            if (gender.isEmpty()) {
                binding.spinnerGender.error = "Please select a gender"
            }

            if (birthDate == "") {
                binding.selectDate.setTextColor(Color.parseColor("#FF0000"))
            }

            if (birthDate != "" && binding.studentName.text!!.isNotEmpty() && binding.studentAddress.text!!.isNotEmpty() && binding.mobileNumber.text!!.isNotEmpty() && binding.feesAmount.text!!.isNotEmpty() && gender.isNotEmpty()) {


                val studentId = studentRef.push().key!!

                fees = "${binding.feesAmount.text}".toDouble()


                if (month > 0) {
                    isAdvance = 1
                }


                val student = Student(
                    studentId,
                    "${binding.studentName.text}",
                    "${binding.mobileNumber.text}",
                    gender,
                    "${binding.studentAddress.text}",
                    className,
                    batch,
                    birthDate,
                    branch,
                    isAdvance,
                    fees,
                    month
                )


                studentRef.child(studentId).setValue(student).addOnCompleteListener {


                    val attendanceId = attendanceRef.push().key!!

                    val attendance = Attendance(
                        attendanceId,
                        student.name,
                        student.className,
                        student.id,
                        student.batch,
                        student.branch,
                        currentDate,
                        1
                    )

                    // Add Attendance list for current date
                    attendanceRef.child(attendanceId).setValue(attendance).addOnCompleteListener {

                        // Add Payment for current date

                        val paymentId = paymentRef.push().key!!

                        val payment = Payment(
                            paymentId,
                            studentId,
                            "${binding.studentName.text}",
                            fees,
                            "$setMonth-$myYear",
                            1,
                            branch,
                            batch,
                            className
                        )

                        paymentRef.child(paymentId).setValue(payment).addOnCompleteListener {


                            Toast.makeText(
                                requireContext(), "Student added successfully", Toast.LENGTH_SHORT
                            ).show()

                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                }
            }

        }




        return binding.root
    }


    private fun setSpinners() {


        genderValue.add("Male")
        genderValue.add("Female")
        genderValue.add("Other")




        for (i in 0..12) {
            monthValue.add(i.toString())
        }

        val genderAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, genderValue
        )

        binding.spinnerGender.setAdapter(genderAdapter)


        val monthAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, monthValue
        )

        binding.selectMonth.setAdapter(monthAdapter)





        binding.spinnerGender.setOnItemClickListener { parent, view, position, id ->

            gender = genderValue[position]
            binding.spinnerGender.error = null
        }

        binding.selectMonth.setOnItemClickListener { parent, view, position, id ->
            month = monthValue[position].toInt()

        }
    }

    private fun resumeFunction() {
        val cal = Calendar.getInstance()

        val myYear = cal.get(Calendar.YEAR)
        val myMonth = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

//        binding.selectDate.text = "Date: $day/${myMonth + 1}/$myYear"

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

        birthDate = "$myYear-$setMonth-$setDate"


        binding.selectDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->


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


                    birthDate = "$year-$setMonth-$setDate"


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