package com.amtron.dronamma.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amtron.dronamma.R
import com.amtron.dronamma.adapter.AdvanceAdapter
import com.amtron.dronamma.adapter.PaymentAdapter
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
import java.util.Calendar


class Payment : Fragment(), PaymentAdapter.ItemClickInterface, AdvanceAdapter.ItemClickInterface {

    private lateinit var binding: FragmentPaymentBinding

    private lateinit var packageManager: PackageManager
    private lateinit var messageDialog: AlertDialog


    private lateinit var paymentList: ArrayList<Payment>
    private lateinit var paymentListNew: ArrayList<Payment>
    private lateinit var advanceList: ArrayList<Student>
    private lateinit var advanceListNew: ArrayList<Student>

    private lateinit var screen: String

    private lateinit var paymentRef: DatabaseReference
    private lateinit var studentRef: DatabaseReference

    private lateinit var paymentRecycler: PaymentAdapter
    private lateinit var advanceRecycler: AdvanceAdapter
    private lateinit var student: Student

    private lateinit var yearValue: ArrayList<String>
    private lateinit var monthValue: ArrayList<String>
    private lateinit var monthValueNumber: ArrayList<String>




    private lateinit var date: String
    private lateinit var currentMonth: String

    private var monthNumber = 0

    private lateinit var month: String
    private lateinit var year: String

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var user: User
    private lateinit var branch: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        packageManager = requireContext().packageManager

        monthValueNumber = arrayListOf()

        for (i in 0..12) {
            monthValueNumber.add(i.toString())
        }


        sharedPreferences =
            requireActivity().getSharedPreferences("Drona", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        user = Gson().fromJson(
            sharedPreferences.getString("user", "").toString(), object : TypeToken<User>() {}.type
        )

        branch = user.branch.toString()

        screen = "regular"

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
        currentMonth="$setMonth-$myYear"




        binding.searchStudentName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // We we add something later

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                paymentListNew.clear()

                for (payment: Payment in paymentList) {

                    if (payment.branch == branch && payment.date == date) {
                        val list: String? = payment.name?.lowercase()?.replace(" ", "")
                        val input: String = s.toString().lowercase().replace(" ", "")

                        if (input.let { list?.contains(it) } == true) {
                            paymentListNew.add(payment)
                        }
                    }

                }

                advanceListNew.clear()



                for (student: Student in advanceList) {

                    if (student.branch == branch) {
                        val list: String? = student.name?.lowercase()?.replace(" ", "")
                        val input: String = s.toString().lowercase().replace(" ", "")

                        if (input.let { list?.contains(it) } == true) {
                            advanceListNew.add(student)
                        }
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
        paymentRecycler = PaymentAdapter(requireContext(), this)
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


                    if (screen == "regular") {
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

                        if (studentData != null && studentData.month == 0 && studentData.paid == 1 && studentData.branch == branch) {

                            advanceList.add(studentData)

                        }
                    }

                    advanceRecycler.updateList(advanceList)

                    if (screen == "advance") {
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
                        if (payment.payment == 1 && payment.date == date && payment.branch == branch) {
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
                        if (payment.payment == 0 && payment.date == date && payment.branch == branch) {
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

    override fun addPayment(student: Student) {


        val mDialog = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_advance_payment, null)

        mDialog.setView(mDialogView)

        val itemPrice = mDialogView.findViewById<EditText>(R.id.itemPrice)
        val cancel = mDialogView.findViewById<Button>(R.id.cancel)
        val pay = mDialogView.findViewById<Button>(R.id.pay)
        val selectMonth = mDialogView.findViewById<AutoCompleteTextView>(R.id.selectMonth)

        val monthAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, monthValueNumber
        )

        selectMonth.setAdapter(monthAdapter)


        selectMonth.setOnItemClickListener { parent, view, position, id ->
            monthNumber = monthValueNumber[position].toInt()
            student.month=monthNumber

        }

        itemPrice.setText(student.fees.toString())

        val alertDialog = mDialog.create()
        alertDialog.show()

        cancel.setOnClickListener {
            alertDialog.dismiss()
        }

        pay.setOnClickListener {


            if (itemPrice.text?.isEmpty() == true) {
                itemPrice.error = "Please enter the fees amount"
            } else {
                val id = student.id.toString()


                if(monthNumber==0){
                    student.paid=0
                }


                student.fees = itemPrice.text.toString().toDouble()

                studentRef.child(id).setValue(student).addOnCompleteListener {


                    // Add Payment for current date

                    val paymentId = paymentRef.push().key!!

                    val payment = Payment(
                        paymentId,
                        student.id,
                        student.name,
                        student.fees,
                        currentMonth,
                        1,
                        branch,
                        student.batch,
                        student.className
                    )

                    paymentRef.child(paymentId).setValue(payment).addOnCompleteListener {


                        Toast.makeText(
                            requireContext(), "Payment for ${student.name} accepted", Toast.LENGTH_SHORT
                        ).show()

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT)
                            .show()
                    }



                    itemPrice.text?.clear()

                    alertDialog.dismiss()

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                }
            }

        }


    }

    override fun sendNotification(id: String) {

//        We will send whatsapp message through this send Notification

        studentRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val data = dataSnapshot.value // Retrieve the data
                // Do something with the data

                student = dataSnapshot.getValue(Student::class.java)!!


                val phoneNumber = "${student.mobile}" // Replace with the recipient's phone number
                val message =
                    "Hello,${student.name}  please complete your payment" // Replace with the message content

                sendWhatsAppMessage(phoneNumber, message)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur
            }
        })
    }

    override fun pay(payment: Payment) {


        val builder = AlertDialog.Builder(requireContext())

        val ans = payment.name
        val id = payment.payment_id
        val month = payment.date

        builder.setTitle("Payment")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to mark student $ans as paid for the date $month?")
        builder.setIcon(R.drawable.baseline_currency_rupee_24)

        //performing positive action
        builder.setPositiveButton("Confirm") { dialogInterface, which ->


            payment.payment = 1

            if (id != null) {
                paymentRef.child(id).setValue(payment).addOnCompleteListener {
                    Toast.makeText(
                        requireContext(),
                        "Payment of ${payment.name} is completed",
                        Toast.LENGTH_SHORT
                    ).show()

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                }
            }

            messageDialog.dismiss()
        }

        //performing negative action
        builder.setNegativeButton("Cancel") { dialogInterface, which ->
            messageDialog.dismiss()
        }
        // Create the AlertDialog
        messageDialog = builder.create()
        // Set other dialog properties
        messageDialog.setCancelable(false)
        messageDialog.show()


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

    fun sendWhatsAppMessage(toNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SEND)
//        intent.putExtra(Intent.EXTRA_TEXT, message)

        intent.setPackage("com.whatsapp")

        intent.type = "text/plain"

        intent.putExtra("android.intent.extra.PHONE_NUMBER", toNumber)

        // Set the message text
        intent.putExtra("android.intent.extra.TEXT", message)


        try {
            requireContext().startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(
                requireContext(), "WhatsApp not Installed in your mobile", Toast.LENGTH_SHORT
            ).show()
        }


    }

}