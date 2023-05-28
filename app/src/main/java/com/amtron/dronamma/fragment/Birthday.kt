package com.amtron.dronamma.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.amtron.dronamma.R
import com.amtron.dronamma.adapter.BirthdayAdapter
import com.amtron.dronamma.adapter.PaymentAdapter
import com.amtron.dronamma.databinding.FragmentBirthdayBinding
import com.amtron.dronamma.model.Birthday
import com.amtron.dronamma.model.Payment
import com.amtron.dronamma.model.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Birthday : Fragment() {


    private lateinit var binding: FragmentBirthdayBinding
    private lateinit var studentRef: DatabaseReference
    private lateinit var birthdayList: ArrayList<Birthday>
    private lateinit var birthdayAdapter: BirthdayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBirthdayBinding.inflate(inflater, container, false)
        studentRef = FirebaseDatabase.getInstance().getReference("Students")

        birthdayList = arrayListOf()


        // populating payment adapter
        binding.birthdayRecycler.layoutManager = LinearLayoutManager(requireActivity())
        birthdayAdapter = BirthdayAdapter()
        binding.birthdayRecycler.adapter = birthdayAdapter

        studentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    birthdayList.clear()

                    for (emSnap in snapshot.children) {
                        val studentData = emSnap.getValue(Student::class.java)


                        if (studentData != null  && isBirthdayToday(studentData.birthday.toString())) {

                            val age = calculateAge(studentData.birthday.toString())

                            birthdayList.add(Birthday(studentData.name, age.toString()))

                        }

                    }

                    birthdayAdapter.updateList(birthdayList)

                    if (birthdayList.isNotEmpty()) {
                        binding.birthdayRecycler.visibility = View.VISIBLE
                        binding.noBirthday.visibility = View.GONE
                    } else {
                        binding.birthdayRecycler.visibility = View.GONE
                        binding.noBirthday.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
            }
        })



        return binding.root
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

    fun isBirthdayToday(birthDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = Calendar.getInstance()
        val date = dateFormat.parse(birthDate)
        val birthday = Calendar.getInstance()
        birthday.time = date

        return today.get(Calendar.MONTH) == birthday.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == birthday.get(Calendar.DAY_OF_MONTH)
    }


}