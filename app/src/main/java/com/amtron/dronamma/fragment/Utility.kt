package com.amtron.dronamma.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amtron.dronamma.R
import com.amtron.dronamma.databinding.FragmentAddStudentBinding
import com.amtron.dronamma.databinding.FragmentUtilityBinding
import java.util.Calendar


class Utility : Fragment() {


    private lateinit var binding: FragmentUtilityBinding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var currentData : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUtilityBinding.inflate(inflater, container, false)

        val cal = Calendar.getInstance()

        val myYear = cal.get(Calendar.YEAR)
        val myMonth = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        currentData = "$myYear-${myMonth+1}-$day"

        sharedPreferences =
            requireActivity().getSharedPreferences("Drona", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()


        var storedDate = sharedPreferences.getString("date", "").toString()


        binding.addStudentForTodayAttendance.setOnClickListener {


            if(storedDate.isEmpty() || storedDate!=currentData){
                editor.putString("date",currentData)
                storedDate = sharedPreferences.getString("date", "").toString()



                // Add Attendance list for current date

                Toast.makeText(requireActivity(),"Attendance added for today's date",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireActivity(),"Attendance already added for today's date",Toast.LENGTH_SHORT).show()
            }



        }


        return binding.root
    }


}