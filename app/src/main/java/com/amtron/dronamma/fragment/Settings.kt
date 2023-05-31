package com.amtron.dronamma.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amtron.dronamma.R
import com.amtron.dronamma.adapter.BatchAndClassAdapter
import com.amtron.dronamma.databinding.FragmentSettingsBinding
import com.amtron.dronamma.model.BatchClassModel
import com.amtron.dronamma.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Settings : Fragment(), BatchAndClassAdapter.ItemClickInterface {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var branch: String
    lateinit var user: User

    private lateinit var batchName: ArrayList<BatchClassModel>
    private lateinit var className: ArrayList<BatchClassModel>


    private lateinit var batchAdapter: BatchAndClassAdapter
    private lateinit var classAdapter: BatchAndClassAdapter


    private lateinit var messageDialog: AlertDialog


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        dbref = FirebaseDatabase.getInstance().getReference("BatchClass")

        batchName = arrayListOf()
        className = arrayListOf()

        sharedPreferences =
            requireActivity().getSharedPreferences("Drona", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        user = Gson().fromJson(
            sharedPreferences.getString("user", "").toString(), object : TypeToken<User>() {}.type
        )

        branch = user.branch.toString()

        binding.submitBatchName.setOnClickListener {

            if (binding.addNewBatch.text?.isNotEmpty() == true) {

                populateDataBase(branch, 1, binding.addNewBatch.text.toString())

            }

        }

        binding.submitClassName.setOnClickListener {
            if (binding.addNewClass.text?.isNotEmpty() == true) populateDataBase(
                branch, 0, binding.addNewClass.text.toString()
            )
        }


        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    batchName = arrayListOf()
                    className = arrayListOf()

                    for (emSnap in snapshot.children) {
                        val batchClassData = emSnap.getValue(BatchClassModel::class.java)

                        if (batchClassData != null) {

                            if (batchClassData.batch == 1) {
                                batchName.add(batchClassData)
                            } else {
                                className.add(batchClassData)
                            }
                        }

                    }


                    binding.classLoading.visibility = View.GONE
                    binding.batchLoading.visibility = View.GONE

                    if(className.isEmpty()){
                        binding.classRecyclerView.visibility = View.GONE
                        binding.classNoData.visibility = View.VISIBLE
                    }else{
                        binding.classRecyclerView.visibility = View.VISIBLE
                        binding.classNoData.visibility = View.GONE
                    }


                    if(batchName.isEmpty()){
                        binding.batchRecyclerView.visibility = View.GONE
                        binding.batchNoData.visibility = View.VISIBLE
                    }else{
                        binding.batchRecyclerView.visibility = View.VISIBLE
                        binding.batchNoData.visibility = View.GONE
                    }

                    batchAdapter.updateList(batchName)
                    classAdapter.updateList(className)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "$error", Toast.LENGTH_SHORT).show()
            }
        })

        binding.batchRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        batchAdapter = BatchAndClassAdapter(this)
        binding.batchRecyclerView.adapter = batchAdapter

        binding.classRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        classAdapter = BatchAndClassAdapter(this)
        binding.classRecyclerView.adapter = classAdapter



        return binding.root
    }

    private fun populateDataBase(branch: String, isBatch: Int, name: String) {
        val id = dbref.push().key!!

        val batchClass = BatchClassModel(
            id, branch, name, isBatch
        )


        dbref.child(id).setValue(batchClass).addOnCompleteListener {
            Toast.makeText(
                requireContext(), "$name Added!", Toast.LENGTH_SHORT
            ).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(field: BatchClassModel) {

        val builder = AlertDialog.Builder(requireContext())

        val ans = field.batchOrClass
        val id = field.id

        builder.setTitle("Delete")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $ans from the list?")
        builder.setIcon(R.drawable.baseline_delete_24)

        //performing positive action
        builder.setPositiveButton("Confirm") { dialogInterface, which ->


            val mTask = id?.let { dbref.child(it).removeValue() }

            mTask?.addOnSuccessListener {

                Toast.makeText(requireContext(), "$ans deleted ", Toast.LENGTH_LONG).show()

            }?.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
            }

            messageDialog.dismiss()
        }

        //performing negative action
        builder.setNegativeButton("Cancel") { dialogInterface, which ->
            messageDialog.dismiss()
        }
        // Create the AlertDialog
        messageDialog = builder.create()
        // Set other dialog properties
        messageDialog.setCancelable(false)
        messageDialog.show()

    }


}