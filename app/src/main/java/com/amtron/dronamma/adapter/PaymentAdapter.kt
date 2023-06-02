package com.amtron.dronamma.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amtron.dronamma.R
import com.amtron.dronamma.model.Payment

class PaymentAdapter(private val context: Context,private val itemClickInterface: ItemClickInterface) :
    RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    private val allData = ArrayList<Payment>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val studentName = itemView.findViewById<TextView>(R.id.attendanceName)!!
        val className = itemView.findViewById<TextView>(R.id.attendanceClass)!!
        val batchName = itemView.findViewById<TextView>(R.id.attendanceBatch)!!
        val sendNotification = itemView.findViewById<TextView>(R.id.sendNotification)!!
        val paymentStatus = itemView.findViewById<TextView>(R.id.paymentStatus)!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment, parent, false)
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
            itemClickInterface.sendNotification(allData[position].student_id.toString())
        }


        if(allData[position].payment == 1){
            holder.paymentStatus.text = "Paid"
            val successColor = ContextCompat.getColor(context, R.color.successColor)
            holder.paymentStatus.setTextColor(successColor)
        }else{
            holder.paymentStatus.text = "Click to Pay"
            val editColor = ContextCompat.getColor(context, R.color.editColor)
            holder.paymentStatus.setTextColor(editColor)
        }


        holder.paymentStatus.setOnClickListener {
            if(allData[position].payment == 0){
                itemClickInterface.pay(allData[position])
            }
        }





    }


    fun updateList(newList: List<Payment>) {
        allData.clear()
        allData.addAll(newList)
        notifyDataSetChanged()

    }


    interface ItemClickInterface {
        fun sendNotification(id: String)
        fun pay(payment: Payment)
    }
}