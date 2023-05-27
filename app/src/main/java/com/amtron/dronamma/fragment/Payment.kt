package com.amtron.dronamma.fragment

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amtron.dronamma.R
import com.amtron.dronamma.adapter.AdvanceAdapter
import com.amtron.dronamma.adapter.AttendanceAdapter
import com.amtron.dronamma.adapter.PaymentAdapter
import com.amtron.dronamma.databinding.FragmentAttendanceBinding
import com.amtron.dronamma.databinding.FragmentPaymentBinding
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
import org.json.JSONObject
import java.util.Calendar

class Payment : Fragment(), PaymentAdapter.ItemClickInterface, AdvanceAdapter.ItemClickInterface {

    private lateinit var binding: FragmentPaymentBinding

    private lateinit var paymentList: ArrayList<Payment>
    private lateinit var paymentListNew: ArrayList<Payment>
    private lateinit var advanceList: ArrayList<Student>
    private lateinit var advanceListNew: ArrayList<Student>

    private lateinit var screen : String

    private lateinit var paymentRef: DatabaseReference
    private lateinit var studentRef: DatabaseReference

    private lateinit var paymentRecycler: PaymentAdapter
    private lateinit var advanceRecycler: AdvanceAdapter
    private lateinit var student: Student

    private lateinit var yearValue: ArrayList<String>
    private lateinit var monthValue: ArrayList<String>

    private lateinit var date : String

    private lateinit var month: String
    private lateinit var year: String

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPaymentBinding.inflate(inflater, container, false)


        sharedPreferences =
            requireActivity().getSharedPreferences("Drona", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        user = Gson().fromJson(
            sharedPreferences.getString("user", "").toString(), object : TypeToken<User>() {}.type
        )

        screen="regular"

        paymentRef = FirebaseDatabase.getInstance().getReference("Payment")
        studentRef = FirebaseDatabase.getInstance().getReference("Students")

        val cal = Calendar.getInstance()

        val myYear = cal.get(Calendar.YEAR)
        val myMonth = cal.get(Calendar.MONTH)

        paymentList = arrayListOf()
        advanceList = arrayListOf()
        advanceListNew = arrayListOf()
        paymentListNew = arrayListOf()
        monthValue = arrayListOf()
        yearValue = arrayListOf()
        month = ""
        year = ""

        setMonthSpinner()
        setYearSpinner()

        val setMonth: String = if (myMonth + 1 < 10) {
            "0${myMonth + 1}"
        } else {
            "${myMonth + 1}"
        }

        date = "$setMonth-$myYear"




        binding.searchStudentName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // We we add something later

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                paymentListNew.clear()

                for (payment: Payment in paymentList) {

                    val list: String? = payment.name?.lowercase()?.replace(" ", "")
                    val input: String = s.toString().lowercase().replace(" ", "")

                    if (input.let { list?.contains(it) } == true) {
                        paymentListNew.add(payment)
                    }
                }

                advanceListNew.clear()


                for(student : Student in advanceList){
                    val list: String? = student.name?.lowercase()?.replace(" ", "")
                    val input: String = s.toString().lowercase().replace(" ", "")

                    if (input.let { list?.contains(it) } == true) {
                        advanceListNew.add(student)
                    }
                }




            }

            override fun afterTextChanged(s: Editable?) {

                // We we add something later
                paymentRecycler.updateList(paymentListNew)
                advanceRecycler.updateList(advanceListNew)
                binding.paid.isChecked = false
                binding.unPaid.isChecked = false
            }

        })


        // populating payment adapter
        binding.paymentRecycler.layoutManager = LinearLayoutManager(requireActivity())
        paymentRecycler = PaymentAdapter(this)
        binding.paymentRecycler.adapter = paymentRecycler


        // populating advance adapter
        binding.advanceRecycler.layoutManager = LinearLayoutManager(requireActivity())
        advanceRecycler = AdvanceAdapter(this)
        binding.advanceRecycler.adapter = advanceRecycler


        paymentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    paymentList.clear()
                    paymentListNew.clear()

                    for (emSnap in snapshot.children) {
                        val paymentData = emSnap.getValue(Payment::class.java)
                        if (paymentData != null && paymentData.branch == user.branch) {
                            paymentList.add(paymentData)
                        }
                    }


                    for (payment: Payment in paymentList) {
                        if (payment.date == date) {
                            paymentListNew.add(payment)
                        }
                    }

                    paymentRecycler.updateList(paymentListNew)


                    if(screen=="regular"){
                        if (paymentListNew.isNotEmpty()) {
                            binding.selectDateLayout.visibility = View.VISIBLE
                            binding.noDataAvailable.visibility = View.GONE
                        } else {
                            binding.noDataAvailable.visibility = View.VISIBLE
                            binding.selectDateLayout.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
            }
        })



        studentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    advanceList.clear()

                    for (emSnap in snapshot.children) {
                        val studentData = emSnap.getValue(Student::class.java)

                        if (studentData != null && studentData.advance!! < 100.00 && studentData.paid == 1 && studentData.branch == user.branch) {

                            advanceList.add(studentData)

                        }
                    }

                    advanceRecycler.updateList(advanceList)

                    if(screen=="advance"){
                        if (advanceList.isNotEmpty()) {
                            binding.advanceRecycler.visibility = View.VISIBLE
                            binding.advanceComplete.visibility = View.GONE
                        } else {
                            binding.advanceRecycler.visibility = View.GONE
                            binding.advanceComplete.visibility = View.VISIBLE
                        }
                    }



                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
            }
        })


        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.regular -> {
                    // Option 1 is selected
                    screen = "regular"
                    binding.advanceRecycler.visibility = View.GONE
                    binding.advanceComplete.visibility = View.GONE
                    binding.selectDateLayout.visibility = View.VISIBLE

                    if (paymentListNew.isNotEmpty()) {
                        binding.paymentRecycler.visibility = View.VISIBLE
                        binding.noDataAvailable.visibility = View.GONE
                    } else {
                        binding.paymentRecycler.visibility = View.GONE
                        binding.noDataAvailable.visibility = View.VISIBLE
                    }

                }

                R.id.advance -> {
                    // Option 2 is selected

                    screen = "advance"

                    binding.selectDateLayout.visibility = View.GONE
                    binding.noDataAvailable.visibility = View.GONE

                    if (advanceList.isNotEmpty()) {
                        binding.advanceRecycler.visibility = View.VISIBLE
                        binding.advanceComplete.visibility = View.GONE
                    } else {
                        binding.advanceRecycler.visibility = View.GONE
                        binding.advanceComplete.visibility = View.VISIBLE
                    }
                }

            }
        }

        // we will add the paid unpaid group later

        binding.paidGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.paid -> {

                    binding.advanceRecycler.visibility = View.GONE
                    binding.advanceComplete.visibility = View.GONE

                    // Option 1 is selected
                    paymentListNew.clear()

                    for (payment: Payment in paymentList) {
                        if (payment.payment == 1  && payment.date==date) {
                            paymentListNew.add(payment)
                        }
                    }

                    if (paymentListNew.isNotEmpty()) {
                        paymentRecycler.updateList(paymentListNew)
                        binding.paymentRecycler.visibility = View.VISIBLE
                        binding.noDataAvailable.visibility = View.GONE
                    } else {
                        binding.paymentRecycler.visibility = View.GONE
                        binding.noDataAvailable.visibility = View.VISIBLE
                    }


                }

                R.id.unPaid -> {
                    // Option 2 is selected


                    binding.advanceRecycler.visibility = View.GONE
                    binding.advanceComplete.visibility = View.GONE

                    paymentListNew.clear()

                    for (payment: Payment in paymentList) {
                        if (payment.payment == 0 && payment.date==date) {
                            paymentListNew.add(payment)
                        }
                    }

                    if (paymentListNew.isNotEmpty()) {
                        paymentRecycler.updateList(paymentListNew)
                        binding.paymentRecycler.visibility = View.VISIBLE
                        binding.noDataAvailable.visibility = View.GONE
                    } else {
                        binding.paymentRecycler.visibility = View.GONE
                        binding.noDataAvailable.visibility = View.VISIBLE
                    }
                }

            }
        }

        return binding.root
    }

    override fun addPayment(id: String) {
        // Going to Student activity
    }

    override fun sendNotification(id: String) {

//        We will send whatsapp message through this send Notification

        studentRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val data = dataSnapshot.value // Retrieve the data
                // Do something with the data

                student = dataSnapshot.getValue(Student::class.java)!!


                Log.d(TAG, "Student: ${student.mobile}")

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
            }
        })
    }

    override fun setCheckBoxTrue(payment: Payment) {
        payment.payment = 1

        paymentRef.child(payment.payment_id.toString()).setValue(payment).addOnCompleteListener {
//            Toast.makeText(
//                requireContext(), "Payment of ${payment.name} is Accepted", Toast.LENGTH_SHORT
//            ).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setCheckBoxFalse(payment: Payment) {
        payment.payment = 0

        paymentRef.child(payment.payment_id.toString()).setValue(payment).addOnCompleteListener {
//            Toast.makeText(
//                requireContext(), "Payment of ${payment.name} is Removed", Toast.LENGTH_SHORT
//            ).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }
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
            requireContext(), android.R.layout.simple_spinner_dropdown_item, monthValue
        )

        binding.spinnerMonth.setAdapter(monthAdapter)

        binding.spinnerMonth.setOnItemClickListener { parent, view, position, id ->


            binding.advanceRecycler.visibility = View.GONE
            binding.advanceComplete.visibility = View.GONE

            month = if (position + 1 < 10) {
                "0${position + 1}"
            } else {
                "${position + 1}"
            }

            date = "$month-$year"

            paymentListNew.clear()


            for (payment: Payment in paymentList) {
                if (payment.date == date) {
                    paymentListNew.add(payment)
                }
            }

            if (paymentListNew.isNotEmpty()) {
                paymentRecycler.updateList(paymentListNew)
                binding.paymentRecycler.visibility = View.VISIBLE
                binding.noDataAvailable.visibility = View.GONE
            } else {
                binding.noDataAvailable.visibility = View.VISIBLE
                binding.paymentRecycler.visibility = View.GONE
            }

        }


    }

    private fun setYearSpinner() {

        val cal = Calendar.getInstance()

        val myYear = cal.get(Calendar.YEAR)

        for (i in 2021..myYear) {
            yearValue.add(i.toString())
        }


        val yearAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, yearValue
        )

        binding.spinnerYear.setAdapter(yearAdapter)

        binding.spinnerYear.setOnItemClickListener { parent, view, position, id ->

            year = yearValue[position]

            binding.advanceRecycler.visibility = View.GONE
            binding.advanceComplete.visibility = View.GONE

         date = "$month-$year"

            paymentListNew.clear()

            for (payment: Payment in paymentList) {
                if (payment.date == date) {
                    paymentListNew.add(payment)
                }
            }

            if (paymentListNew.isNotEmpty()) {
                paymentRecycler.updateList(paymentListNew)
                binding.paymentRecycler.visibility = View.VISIBLE
                binding.noDataAvailable.visibility = View.GONE
            } else {
                binding.noDataAvailable.visibility = View.VISIBLE
                binding.paymentRecycler.visibility = View.GONE
            }

        }

    }


}