package com.amtron.dronamma

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.amtron.dronamma.activity.HomeActivity
import com.amtron.dronamma.activity.LoginActivity
import com.amtron.dronamma.databinding.ActivityHomeBinding
import com.amtron.dronamma.databinding.ActivityMainBinding
import com.amtron.dronamma.model.Date
import com.amtron.dronamma.service.ConnectionLiveData
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logo.visibility = View.GONE
        binding.noInternet.visibility = View.GONE


        sharedPreferences = this.getSharedPreferences("Drona", MODE_PRIVATE)
        editor = sharedPreferences.edit()




//        editor.remove("user")
//        editor.apply()




        val networkConnection = ConnectionLiveData(application)

        networkConnection.observe(this, Observer {
            if (it) {

                binding.logo.visibility = View.VISIBLE
                binding.noInternet.visibility = View.GONE

                val user = sharedPreferences.getString("user", "").toString()
                if (user.isEmpty()) {


                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }


            } else {
                binding.logo.visibility = View.GONE
                binding.noInternet.visibility = View.VISIBLE
            }
        })
    }


}