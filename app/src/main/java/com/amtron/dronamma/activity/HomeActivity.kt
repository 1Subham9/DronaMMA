package com.amtron.dronamma.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.amtron.dronamma.R
import com.amtron.dronamma.adapter.ViewPagerAdapter
import com.amtron.dronamma.fragment.AddStudent
import com.amtron.dronamma.fragment.Attendance
import com.amtron.dronamma.fragment.Payment
import com.amtron.dronamma.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeActivity : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        sharedPreferences = this.getSharedPreferences("Drona", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        user = Gson().fromJson(
            sharedPreferences.getString("user", "").toString(), object : TypeToken<User>() {}.type
        )


        Toast.makeText(this, "${user.name}", Toast.LENGTH_SHORT).show()

        val fragmentList = arrayListOf<Fragment>(
            Attendance(), AddStudent(), Payment()
        )

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)


        val adapter = ViewPagerAdapter(
            fragmentList, this.supportFragmentManager, lifecycle
        )

        val viewPage = findViewById<ViewPager2>(R.id.viewPager)

        viewPage.adapter = adapter

        bottomNav.setOnItemSelectedListener {


            when (it.itemId) {

                R.id.attendance -> viewPage.currentItem = 0

                R.id.addStudent -> viewPage.currentItem = 1

                R.id.payment -> viewPage.currentItem = 2
            }

            return@setOnItemSelectedListener true
        }


        viewPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                when (position) {

                    0 -> bottomNav.selectedItemId = R.id.attendance
                    1 -> bottomNav.selectedItemId = R.id.addStudent
                    2 -> bottomNav.selectedItemId = R.id.payment

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


    }


}