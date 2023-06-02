package com.amtron.dronamma.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amtron.dronamma.R
import com.amtron.dronamma.model.Payment
import com.amtron.dronamma.model.Student

class AdvanceAdapter(private val itemClickInterface: ItemClickInterface) :
    RecyclerView.Adapter<AdvanceAdapter.ViewHolder>() {

    private val allData = ArrayList<Student>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val studentName = itemView.findViewById<TextView>(R.id.attendanceName)!!
        val className = itemView.findViewById<TextView>(R.id.attendanceClass)!!
        val batchName = itemView.findViewById<TextView>(R.id.attendanceBatch)!!
        val sendNotification = itemView.findViewById<TextView>(R.id.sendNotification)!!
        val addPayment = itemView.findViewById<TextView>(R.id.addPayment)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_advance, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.studentName.text = allData[position].name
        holder.className.text = "Class: ${allData[position].className}"
        holder.batchName.text = "Batch: ${allData[position].batch},"

        holder.sendNotification.setOnClickListener {
            itemClickInterface.sendNotification(allData[position].id.toString())
        }

        holder.addPayment.setOnClickListener {
            itemClickInterface.addPayment(allData[position])
        }

    }


    fun updateList(newList: List<Student>) {
        allData.clear()
        allData.addAll(newList)
        notifyDataSetChanged()

    }


    interface ItemClickInterface {
        fun addPayment(student: Student)
        fun sendNotification(id: String)
    }
}