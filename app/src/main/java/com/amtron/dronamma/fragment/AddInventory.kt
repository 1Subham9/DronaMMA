package com.amtron.dronamma.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amtron.dronamma.R
import com.amtron.dronamma.databinding.FragmentAddInventoryBinding

class AddInventory : Fragment() {


    private lateinit var binding : FragmentAddInventoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddInventoryBinding.inflate(inflater,container,false)







        return binding.root
    }


}