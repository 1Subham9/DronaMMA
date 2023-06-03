package com.amtron.dronamma.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amtron.dronamma.R
import com.amtron.dronamma.model.Birthday
import com.amtron.dronamma.model.Student

class BirthdayAdapter : RecyclerView.Adapter<BirthdayAdapter.ViewHolder>() {

    private val allData = ArrayList<Birthday>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val studentName = itemView.findViewById<TextView>(R.id.birthdayName)!!
        val studentAge = itemView.findViewById<TextView>(R.id.birthdayAge)!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.birthday_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.studentName.text = allData[position].name
        holder.studentAge.text = "Age: ${allData[position].age}"

    }

    fun updateList(newList: List<Birthday>) {
        allData.clear()
        allData.addAll(newList)
        notifyDataSetChanged()

    }


}