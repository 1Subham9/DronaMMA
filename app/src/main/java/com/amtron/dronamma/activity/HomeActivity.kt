package com.amtron.dronamma.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.amtron.dronamma.R
import com.amtron.dronamma.adapter.ViewPagerAdapter
import com.amtron.dronamma.databinding.ActivityHomeBinding
import com.amtron.dronamma.fragment.AddStudent
import com.amtron.dronamma.fragment.Attendance
import com.amtron.dronamma.helper.Common
import com.amtron.dronamma.helper.Common.Companion.branch
import com.amtron.dronamma.helper.Common.Companion.paymentRef
import com.amtron.dronamma.helper.Common.Companion.studentRef
import com.amtron.dronamma.helper.Common.Companion.user
import com.amtron.dronamma.model.Date
import com.amtron.dronamma.model.Payment
import com.amtron.dronamma.model.Student
import com.amtron.dronamma.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class HomeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBar: ActionBar

    private lateinit var studentList: ArrayList<Student>



    private var flag: Boolean = true


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        sharedPreferences = this.getSharedPreferences("Drona", MODE_PRIVATE)
        editor = sharedPreferences.edit()


        user = Gson().fromJson(
            sharedPreferences.getString("user", "").toString(), object : TypeToken<User>() {}.type
        )

        branch = user?.branch.toString()

        studentList = arrayListOf()

        val cal = Calendar.getInstance()
        val myYear = cal.get(Calendar.YEAR)
        val myMonth = cal.get(Calendar.MONTH)


        val setMonth: String = if (myMonth + 1 < 10) {
            "0${myMonth + 1}"
        } else {
            "${myMonth + 1}"
        }

        val currentMonth = "$setMonth-$myYear"


     studentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    studentList = arrayListOf()

                    for (emSnap in snapshot.children) {
                        val studentData = emSnap.getValue(Student::class.java)

                        if (studentData != null) {
                            studentList.add(studentData)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "$error", Toast.LENGTH_SHORT).show()
            }
        })





        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {

                paymentRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {

                            for (emSnap in snapshot.children) {
                                val paymentData = emSnap.getValue(Payment::class.java)

                                if (paymentData != null && paymentData.date.toString() == currentMonth) {
                                    flag = false
                                }
                            }

                            if (flag) {
                                enterMonthlyData( currentMonth)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@HomeActivity, "$error", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }


        actionBar = supportActionBar!!

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile,
                R.id.nav_add_inventory,
                R.id.nav_birthday,
                R.id.nav_settings,
                R.id.nav_utility
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Check if the destination fragment is the one you want to target

//            if (destination.id == R.id.nav_profile) {
//                // Run your specific function here
//                bottomCloseNavOpen()
//            }
//
//            if (destination.id == R.id.nav_settings) {
//                // Run your specific function here
//                bottomCloseNavOpen()
//            }
//
//            if (destination.id == R.id.nav_add_inventory) {
//                // Run your specific function here
//                bottomCloseNavOpen()
//            }
//
//            if (destination.id == R.id.nav_birthday) {
//                // Run your specific function here
//                bottomCloseNavOpen()
//            }

            bottomCloseNavOpen()
        }


        val fragmentList = arrayListOf<Fragment>(
            Attendance(), AddStudent(), com.amtron.dronamma.fragment.Payment()
        )

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)


        val adapter = ViewPagerAdapter(
            fragmentList, this.supportFragmentManager, lifecycle
        )

        val viewPage = findViewById<ViewPager2>(R.id.viewPager)

        viewPage.adapter = adapter

        bottomNav.setOnItemSelectedListener {


            when (it.itemId) {

                R.id.attendance -> {
                    viewPage.currentItem = 0
                    navCloseBottomOpen()
                    actionBar.title = "Drona"

                }

                R.id.addStudent -> {
                    viewPage.currentItem = 1
                    navCloseBottomOpen()
                    actionBar.title = "Drona"

                }

                R.id.payment -> {
                    viewPage.currentItem = 2
                    navCloseBottomOpen()
                    actionBar.title = "Drona"

                }
            }

            return@setOnItemSelectedListener true
        }



        viewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                when (position) {

                    0 -> {
                        bottomNav.selectedItemId = R.id.attendance
                    }

                    1 -> {
                        bottomNav.selectedItemId = R.id.addStudent
                    }

                    2 -> {
                        bottomNav.selectedItemId = R.id.payment
                    }

                }

                super.onPageSelected(position)
            }


        })


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                when (viewPage.currentItem) {
                    2 -> {
                        viewPage.currentItem = 1
                    }

                    1 -> {
                        viewPage.currentItem = 0
                    }

                    else -> {
                        finish()
                    }
                }

            }
        }

        onBackPressedDispatcher.addCallback(callback)

        navCloseBottomOpen()
        actionBar.title = "Drona"


    }

    private fun enterMonthlyData( currentMonth: String) {


            for (studentData: Student in studentList) {


                if (studentData.paid == 0) {

                    val paymentId = paymentRef.push().key!!

                    val payment = Payment(
                        paymentId,
                        studentData.id,
                        studentData.name,
                        studentData.fees,
                        currentMonth,
                        0,
                        Common.branch,
                        studentData.batch,
                        studentData.className
                    )

                    Common.paymentRef.child(paymentId).setValue(payment).addOnCompleteListener {


                        Toast.makeText(
                            this@HomeActivity,
                            "All pending payment updated for this month",
                            Toast.LENGTH_SHORT
                        ).show()

                    }.addOnFailureListener {
                        Toast.makeText(
                            this@HomeActivity, "Error: $it", Toast.LENGTH_SHORT
                        ).show()
                    }


                } else {


                    if (studentData.month!! > 0) {

                        studentData.month = studentData.month!! - 1

                        studentRef.child(studentData.id.toString()).setValue(studentData)
                            .addOnCompleteListener {


                                Toast.makeText(
                                    this@HomeActivity,
                                    "All pending payment updated for this month",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@HomeActivity, "Error: $it", Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }


    }

    private fun bottomCloseNavOpen() {
        findViewById<View>(R.id.app_bar_main).visibility = View.VISIBLE
        findViewById<View>(R.id.bottom_nav_view).visibility = View.GONE
    }

    private fun navCloseBottomOpen() {
        findViewById<View>(R.id.app_bar_main).visibility = View.GONE
        findViewById<View>(R.id.bottom_nav_view).visibility = View.VISIBLE
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}


