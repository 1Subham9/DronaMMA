package com.amtron.dronamma.helper

import android.content.SharedPreferences
import com.amtron.dronamma.model.Attendance
import com.amtron.dronamma.model.Inventory
import com.amtron.dronamma.model.Payment
import com.amtron.dronamma.model.Student
import com.amtron.dronamma.model.User
import com.google.firebase.database.FirebaseDatabase

class Common {

    companion object {
        var studentList: ArrayList<Student> = arrayListOf()
        var inventoryList: ArrayList<Inventory> = arrayListOf()
        var paymentList: ArrayList<Payment> = arrayListOf()
        var attendanceList: ArrayList<Attendance> = arrayListOf()


        val paymentRef = FirebaseDatabase.getInstance().getReference("Payment")
        val studentRef = FirebaseDatabase.getInstance().getReference("Students")
        val attendanceRef = FirebaseDatabase.getInstance().getReference("Attendance")
        val inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory")
        val batchClassRef = FirebaseDatabase.getInstance().getReference("BatchClass")

        var user: User? = null
        var branch: String? = null
    }
}