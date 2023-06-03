package com.amtron.dronamma.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amtron.dronamma.R
import com.amtron.dronamma.model.Attendance
import com.amtron.dronamma.model.BatchClassModel

class AttendanceAdapter(private val itemClickInterface: ItemClickInterface) :
    RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    private val allData = ArrayList<Attendance>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val studentName = itemView.findViewById<TextView>(R.id.attendanceName)!!
        val className = itemView.findViewById<TextView>(R.id.attendanceClass)!!
        val batchName = itemView.findViewById<TextView>(R.id.attendanceBatch)!!
        val checkDetails = itemView.findViewById<TextView>(R.id.attendanceCheckDetails)!!
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox_attendance)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.student_attendance_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.studentName.text = allData[position].name
        holder.className.text = "Class: ${allData[position].className}"
        holder.batchName.text = "Batch: ${allData[position].batch}"

        holder.checkDetails.setOnClickListener {
            itemClickInterface.onCheckDetails(allData[position].studentId.toString())
        }

        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            // Do something when the checkbox state changes
            if (isChecked) {
                itemClickInterface.setCheckBoxTrue(allData[position])
            } else {
                itemClickInterface.setCheckBoxFalse(allData[position])
            }
        }

        holder.checkBox.isChecked = allData[position].present == 1

    }


    fun updateList(newList: List<Attendance>) {
        allData.clear()
        allData.addAll(newList)
        notifyDataSetChanged()

    }


    interface ItemClickInterface {
        fun onCheckDetails(id: String)
        fun setCheckBoxTrue(attendance: Attendance)
        fun setCheckBoxFalse(attendance: Attendance)
    }
}