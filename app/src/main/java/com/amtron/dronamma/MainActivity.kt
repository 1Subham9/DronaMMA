package com.amtron.dronamma

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.amtron.dronamma.activity.HomeActivity
import com.amtron.dronamma.activity.LoginActivity
import com.amtron.dronamma.service.ConnectionLiveData

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sharedPreferences = this.getSharedPreferences("Drona", MODE_PRIVATE)
        editor = sharedPreferences.edit()





        val networkConnection = ConnectionLiveData(application)

        networkConnection.observe(this, Observer {
            if(it){
                Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show()


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



            }else{
                Toast.makeText(this,"DisConnected",Toast.LENGTH_SHORT).show()
            }
        })
    }


}