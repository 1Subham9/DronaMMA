package com.amtron.dronamma.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amtron.dronamma.R
import com.amtron.dronamma.model.BatchClassModel

class BatchAndClassAdapter(private val itemClickInterface: ItemClickInterface) :
    RecyclerView.Adapter<BatchAndClassAdapter.ViewHolder>(){

    private val allData = ArrayList<BatchClassModel>()


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemData = itemView.findViewById<TextView>(R.id.batchClassName)!!
        val deleteData = itemView.findViewById<ImageView>(R.id.deleteBatchClassName)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.batch_class_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemData.text = allData[position].batchOrClass

        holder.deleteData.setOnClickListener {
            itemClickInterface.onItemClick(allData[position])
        }
    }


    fun updateList(newList: List<BatchClassModel>) {
        allData.clear()
        allData.addAll(newList)
        notifyDataSetChanged()

    }


    interface ItemClickInterface {
        fun onItemClick(field: BatchClassModel)
    }

}