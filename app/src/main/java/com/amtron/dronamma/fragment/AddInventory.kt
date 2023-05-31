package com.amtron.dronamma.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amtron.dronamma.R
import com.amtron.dronamma.adapter.InventoryAdapter
import com.amtron.dronamma.databinding.FragmentAddInventoryBinding
import com.amtron.dronamma.model.Inventory
import com.amtron.dronamma.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddInventory : Fragment(), InventoryAdapter.ItemClickInterface {


    private lateinit var binding: FragmentAddInventoryBinding
    private lateinit var inventoryRef: DatabaseReference
    private lateinit var inventoryAdapter: InventoryAdapter


    private lateinit var inventoryList: ArrayList<Inventory>
    private lateinit var messageDialog: AlertDialog

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var branch: String
    private lateinit var user: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        // Inflate the layout for this fragment
        binding = FragmentAddInventoryBinding.inflate(inflater, container, false)


        sharedPreferences =
            requireActivity().getSharedPreferences("Drona", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()


        user = Gson().fromJson(
            sharedPreferences.getString("user", "").toString(), object : TypeToken<User>() {}.type
        )


        branch = user.branch.toString()


        inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory")

        inventoryList = arrayListOf()


        //Populating Adapter
        binding.inventoryRecycler.layoutManager = LinearLayoutManager(requireActivity())
        inventoryAdapter = InventoryAdapter(this)
        binding.inventoryRecycler.adapter = inventoryAdapter


        //Adding New Item
        binding.addItem.setOnClickListener {
            if (binding.itemName.text?.isEmpty() == true && binding.itemName.text?.isEmpty() == true) {
                binding.itemName.error = "Please Enter Item Name"
                binding.itemPrice.error = "Please Enter Item Price"
            } else if (binding.itemName.text?.isEmpty() == true) {
                binding.itemName.error = "Please Enter Item Name"
            } else if (binding.itemPrice.text?.isEmpty() == true) {
                binding.itemPrice.error = "Please Enter Item Price"
            } else {
                val id = inventoryRef.push().key!!

                val inventory = Inventory(
                    id,
                    binding.itemName.text.toString(),
                    branch,
                    binding.itemPrice.text.toString().toDouble(),
                    0
                )

                inventoryRef.child(id).setValue(inventory).addOnCompleteListener {


                    Toast.makeText(
                        requireContext(),
                        "${binding.itemName.text} added to the Inventory",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.itemName.text?.clear()
                    binding.itemPrice.text?.clear()

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                }
            }

        }


        // Get Data from firebase
        inventoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {


                    inventoryList.clear()

                    for (emSnap in snapshot.children) {
                        val inventoryData = emSnap.getValue(Inventory::class.java)

                        if (inventoryData != null && inventoryData.branch==branch) {
                            inventoryList.add(inventoryData)
                        }
                    }

                    inventoryAdapter.updateList(inventoryList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "$error", Toast.LENGTH_SHORT).show()
            }
        })





        return binding.root
    }

    override fun addItem(inventory: Inventory) {
        inventoryRef.child(inventory.id.toString()).setValue(inventory).addOnCompleteListener {
            Toast.makeText(
                requireContext(), "Item ${inventory.name} is updated", Toast.LENGTH_SHORT
            ).show()

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }
    }

    override fun deleteItem(inventory: Inventory) {
        val builder = AlertDialog.Builder(requireContext())

        val ans = inventory.name
        val id = inventory.id

        builder.setTitle("Delete")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $ans from the inventory?")
        builder.setIcon(R.drawable.baseline_delete_24)

        //performing positive action
        builder.setPositiveButton("Confirm") { dialogInterface, which ->


            val mTask = id?.let { inventoryRef.child(it).removeValue() }

            mTask?.addOnSuccessListener {

                Toast.makeText(requireContext(), "Item $ans deleted ", Toast.LENGTH_LONG).show()

            }?.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Deleting Err ${error.message}", Toast.LENGTH_LONG)
                    .show()
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

    override fun updateItem(inventory: Inventory) {
        val mDialog = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_inventory_item, null)

        mDialog.setView(mDialogView)

        val itemName = mDialogView.findViewById<EditText>(R.id.itemName)
        val itemPrice = mDialogView.findViewById<EditText>(R.id.itemPrice)
        val cancel = mDialogView.findViewById<Button>(R.id.cancel)
        val update = mDialogView.findViewById<Button>(R.id.update)

        itemName.setText(inventory.name)
        itemPrice.setText(inventory.price.toString())

        val alertDialog = mDialog.create()
        alertDialog.show()

        cancel.setOnClickListener {
            alertDialog.dismiss()
        }

        update.setOnClickListener {


            if (itemName.text?.isEmpty() == true && itemName.text?.isEmpty() == true) {
                itemName.error = "Please Enter Item Name"
                itemPrice.error = "Please Enter Item Price"
            } else if (itemName.text?.isEmpty() == true) {
                itemName.error = "Please Enter Item Name"
            } else if (itemPrice.text?.isEmpty() == true) {
                itemPrice.error = "Please Enter Item Price"
            } else {
                val id = inventory.id.toString()


                inventory.name=itemName.text.toString()
                inventory.price =itemPrice.text.toString().toDouble()

                inventoryRef.child(id).setValue(inventory).addOnCompleteListener {


                    Toast.makeText(
                        requireContext(),
                        "${itemName.text} updated to the inventory",
                        Toast.LENGTH_SHORT
                    ).show()



                    itemName.text?.clear()
                    itemPrice.text?.clear()

                    alertDialog.dismiss()

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


}