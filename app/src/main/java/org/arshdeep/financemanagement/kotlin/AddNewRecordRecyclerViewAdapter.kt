package org.arshdeep.financemanagement.kotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.arshdeep.financemanagement.R
import java.util.*
import kotlin.collections.ArrayList

class AddNewRecordRecyclerViewAdapter :
    RecyclerView.Adapter<AddNewRecordRecyclerViewHolder>()
{
    val store: ArrayList<Dictionary<String?, Any>> = ArrayList()
    fun addItem(personName: String,
        uniqueID: Int): Boolean
    {
        val dictionary: Dictionary<String?, Any> = Hashtable(0x5)
        dictionary.put("name", personName)
        dictionary.put("id", uniqueID)
        return store.add(dictionary)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): AddNewRecordRecyclerViewHolder
    {
        val inflatedView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_add_new_record, parent, false)
        return AddNewRecordRecyclerViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: AddNewRecordRecyclerViewHolder,
        position: Int)
    {
        val data = store[position]
        holder.setAmount(data["id"] as Double)
        holder.setName(data["name"] as String)
    }

    override fun getItemCount(): Int
    {
        return (store.size)
    }
}