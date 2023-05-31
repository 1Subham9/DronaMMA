package com.amtron.dronamma.adapter

import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amtron.dronamma.R
import com.amtron.dronamma.model.Inventory
import com.google.android.material.button.MaterialButton

class InventoryAdapter(private val itemClickInterface: ItemClickInterface) :
    RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    private val allData = ArrayList<Inventory>()


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemNameInventory = itemView.findViewById<TextView>(R.id.itemNameInventory)!!
        val itemValue = itemView.findViewById<AutoCompleteTextView>(R.id.itemValue)!!
        val incrementItem = itemView.findViewById<MaterialButton>(R.id.incrementItem)!!
        val decrementItem = itemView.findViewById<MaterialButton>(R.id.decrementItem)!!
        val itemSubmit = itemView.findViewById<MaterialButton>(R.id.itemSubmit)!!
        val itemDelete = itemView.findViewById<MaterialButton>(R.id.itemDelete)!!
        val itemEdit = itemView.findViewById<MaterialButton>(R.id.itemEdit)!!




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.inventory_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemNameInventory.text = "${allData[position].name}\n₹${allData[position].price}"
        holder.itemValue.text =
            Editable.Factory.getInstance().newEditable(allData[position].quantity.toString())


        if (allData[position].quantity == 0) {
            holder.decrementItem.visibility = View.GONE
        }

        holder.itemValue.inputType = InputType.TYPE_CLASS_NUMBER

        holder.incrementItem.setOnClickListener {

            val currentText = holder.itemValue.text.toString()// Get the current text

            val currentValue: Int = if (holder.itemValue.text.isEmpty()) {
                0
            } else {
                currentText.toInt()
            } // Convert the text to an integer


            val increasedValue = currentValue + 1 // Increase the value

            if (increasedValue > 0) {
                holder.decrementItem.visibility = View.VISIBLE
            }


            val newText = increasedValue.toString() // Convert the value back to a string
            holder.itemValue.setText(newText)
        }


        holder.decrementItem.setOnClickListener {

            val currentText = holder.itemValue.text.toString()// Get the current text
            val currentValue: Int = if (holder.itemValue.text.isEmpty()) {
                0
            } else {
                currentText.toInt()
            } // Convert the text to an integer

            var decreasedValue = currentValue - 1 // decrease the value

            if (currentValue == 0) {
                decreasedValue = 0
            }

            if (decreasedValue == 0) {
                holder.decrementItem.visibility = View.GONE
            }

            val newText = decreasedValue.toString() // Convert the value back to a string
            holder.itemValue.setText(newText)
        }


        holder.itemSubmit.setOnClickListener {
            val currentValue: Int = if (holder.itemValue.text.isEmpty()) {
                0
            } else {
                holder.itemValue.text.toString().toInt()
            } // Convert the text to an integer

            allData[position].quantity = currentValue
            itemClickInterface.addItem(allData[position])
        }

        holder.itemDelete.setOnClickListener {
            itemClickInterface.deleteItem(allData[position])
        }

        holder.itemEdit.setOnClickListener {
            itemClickInterface.updateItem(allData[position])
        }


    }

    fun updateList(newList: List<Inventory>) {
        allData.clear()
        allData.addAll(newList)
        notifyDataSetChanged()

    }

    interface ItemClickInterface {
        fun addItem(inventory: Inventory)
        fun deleteItem(inventory: Inventory)
        fun updateItem(inventory: Inventory)
    }
}