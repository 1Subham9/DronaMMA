package com.amtron.dronamma.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amtron.dronamma.R
import com.amtron.dronamma.activity.LoginActivity
import com.amtron.dronamma.activity.RegisterActivity
import com.amtron.dronamma.databinding.FragmentProfileBinding
import com.amtron.dronamma.databinding.FragmentSettingsBinding
import com.amtron.dronamma.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Profile : Fragment() {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    lateinit var user: User

    private lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireActivity().getSharedPreferences("Drona", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        user = Gson().fromJson(
            sharedPreferences.getString("user", "").toString(), object : TypeToken<User>() {}.type
        )


        binding.userName.text = user.name
        binding.userEmail.text = user.UserEmail
        binding.userMobile.text = user.mobile.toString()


        binding.logout.setOnClickListener {

            editor.remove("user")
            editor.apply()


            val intent = Intent (requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()

        }


        return binding.root
    }




}