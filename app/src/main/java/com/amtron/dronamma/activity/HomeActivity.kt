package com.amtron.dronamma.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
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
import com.amtron.dronamma.fragment.Payment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.appBarMain.toolbar)


        actionBar = supportActionBar!!

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile, R.id.nav_birthday
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Check if the destination fragment is the one you want to target
            if (destination.id == R.id.nav_profile) {
                // Run your specific function here
                bottomCloseNavOpen()
            }

            if (destination.id == R.id.nav_birthday) {
                // Run your specific function here
                bottomCloseNavOpen()
            }
        }


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

                R.id.attendance -> {
                    viewPage.currentItem = 0
                    navCloseBottomOpen()
                    actionBar.title = "Drona"

                }

                R.id.addStudent ->{
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
                    2 ->{
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


