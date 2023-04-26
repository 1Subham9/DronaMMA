package com.amtron.dronamma.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.amtron.dronamma.R
import com.amtron.dronamma.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeActivity : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    lateinit var user : User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        sharedPreferences = this.getSharedPreferences("Drona", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        user = Gson().fromJson(sharedPreferences.getString("user", "").toString(),
            object : TypeToken<User>() {}.type)


        Toast.makeText(this, "${user.name}", Toast.LENGTH_SHORT).show()


    }
}