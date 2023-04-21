package com.amtron.dronamma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.amtron.dronamma.service.ConnectionLiveData

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val networkConnection = ConnectionLiveData(application)

        networkConnection.observe(this, Observer {
            if(it){
                Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"DisConnected",Toast.LENGTH_SHORT).show()
            }
        })
    }


}