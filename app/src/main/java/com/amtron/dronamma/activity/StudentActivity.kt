package com.amtron.dronamma.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.amtron.dronamma.R
import com.amtron.dronamma.databinding.ActivityStudentBinding
import com.amtron.dronamma.helper.Common
import com.amtron.dronamma.helper.Common.Companion.attendanceList
import com.amtron.dronamma.model.Attendance
import com.amtron.dronamma.model.Payment
import com.amtron.dronamma.model.Student
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates


class StudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentBinding
    private lateinit var student: Student
    private lateinit var id: String
    private lateinit var month: String
    private lateinit var year: String

    private lateinit var yearValue: ArrayList<String>
    private lateinit var monthValue: ArrayList<String>
    private var flag by Delegates.notNull<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val bundle = intent.extras!!

        supportActionBar?.title = "Student Details"

        id = bundle.getString("id").toString()


        monthValue = arrayListOf()
        yearValue = arrayListOf()
        month = ""
        year = ""


        val cal = Calendar.getInstance()
        year = "${cal.get(Calendar.YEAR)}"
        val myMonth = cal.get(Calendar.MONTH)


        month = if (myMonth + 1 < 10) {
            "0${myMonth + 1}"
        } else {
            "${myMonth + 1}"
        }


        setMonthSpinner()
        setYearSpinner()



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

        var present = 0f
        var absent = 0f

        for (attendance: Attendance in attendanceList) {
            if (attendance.studentId == id) {

                if (attendance.present == 1) {
                    present += 1f
                } else {
                    absent += 1f
                }
            }
        }

        setPieChartValue(present, absent)

        binding.allTime.isChecked = true


        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.allTime -> {
                    // Option 1 is selected
                    binding.selectDateLayout.visibility = View.GONE

                    setPieChart()

                }

                R.id.monthly -> {
                    // Option 2 is selected
                    binding.selectDateLayout.visibility = View.VISIBLE
                    binding.monthLayout.visibility = View.VISIBLE

                    flag=false

                    setPieChart(month, year)

                }

                R.id.yearly -> {
                    // Option 3 is selected
                    binding.selectDateLayout.visibility = View.VISIBLE
                    binding.monthLayout.visibility = View.GONE

                    flag=true

                    setPieChart(year)
                }
            }
        }


    }

    private fun setPieChartValue(present: Float, absent: Float) {
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.description.isEnabled = false


        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(present, "Present"))
        entries.add(PieEntry(absent, "Absent"))

        val dataSet = PieDataSet(entries, "Attendance % Pie Chart")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()


        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)

        binding.pieChart.data = pieData
        binding.pieChart.invalidate() // Refresh the chart


        binding.pieChart.centerText = "Attendance"
        binding.pieChart.setCenterTextSize(18f)
        binding.pieChart.holeRadius = 50f
    }

    private fun setPieChart() {

        var present = 0f
        var absent = 0f

        for (attendance: Attendance in attendanceList) {
            if (attendance.studentId == id) {

                if (attendance.present == 1) {
                    present += 1f
                } else {
                    absent += 1f
                }
            }
        }

        setPieChartValue(present, absent)

    }


    private fun setPieChart(month: String, year: String) {

        var present = 0f
        var absent = 0f

        for (attendance: Attendance in attendanceList) {
            if (attendance.studentId == id) {

                if (checkMonth(attendance.date.toString(), "$year-$month")) {
                    if (attendance.present == 1) {
                        present += 1f
                    } else {
                        absent += 1f
                    }
                }

            }
        }

        setPieChartValue(present, absent)

    }

    private fun setPieChart(year: String) {

        var present = 0f
        var absent = 0f

        for (attendance: Attendance in attendanceList) {
            if (attendance.studentId == id) {

                if (checkYear(attendance.date.toString(), year)) {
                    if (attendance.present == 1) {
                        present += 1f
                    } else {
                        absent += 1f
                    }
                }
            }
        }

        setPieChartValue(present, absent)

    }


    private fun checkMonth(inputDate: String, providedDate: String): Boolean {
        val convertedDate = inputDate.substring(0, inputDate.length - 3)
        return providedDate == convertedDate
    }


    private fun checkYear(inputDate: String, providedDate: String): Boolean {
        val convertedDate = inputDate.substring(0, inputDate.length - 6)
        return providedDate == convertedDate
    }


    private fun setMonthSpinner() {

        monthValue.add("JAN")
        monthValue.add("FEB")
        monthValue.add("MAR")
        monthValue.add("APR")
        monthValue.add("MAY")
        monthValue.add("JUN")
        monthValue.add("JUL")
        monthValue.add("AUG")
        monthValue.add("SEP")
        monthValue.add("OCT")
        monthValue.add("NOV")
        monthValue.add("DEC")


        val monthAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, monthValue
        )

        binding.spinnerMonth.setAdapter(monthAdapter)

        binding.spinnerMonth.setOnItemClickListener { parent, view, position, id ->


            month = if (position + 1 < 10) {
                "0${position + 1}"
            } else {
                "${position + 1}"
            }

            setPieChart(month, year)

        }


    }

    private fun setYearSpinner() {

        val cal = Calendar.getInstance()

        val myYear = cal.get(Calendar.YEAR)

        for (i in 2021..myYear) {
            yearValue.add(i.toString())
        }


        val yearAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, yearValue
        )

        binding.spinnerYear.setAdapter(yearAdapter)

        binding.spinnerYear.setOnItemClickListener { parent, view, position, id ->

            year = yearValue[position]


            if(flag){
                setPieChart(year)
            }else{
                setPieChart(month, year)
            }


        }

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