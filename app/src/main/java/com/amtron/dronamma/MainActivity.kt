package com.amtron.dronamma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.amtron.dronamma.activity.LoginActivity
import com.amtron.dronamma.service.ConnectionLiveData

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        val networkConnection = ConnectionLiveData(application)

        networkConnection.observe(this, Observer {
            if(it){
                Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

            }else{
                Toast.makeText(this,"DisConnected",Toast.LENGTH_SHORT).show()
            }
        })
    }


}