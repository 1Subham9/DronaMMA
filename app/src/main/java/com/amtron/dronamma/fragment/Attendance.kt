package com.amtron.dronamma.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amtron.dronamma.R
import com.amtron.dronamma.databinding.FragmentAddStudentBinding
import com.amtron.dronamma.databinding.FragmentAttendanceBinding


class Attendance : Fragment() {

    private lateinit var binding: FragmentAttendanceBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAttendanceBinding.inflate(inflater, container, false)




        return binding.root
    }


}