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
        amount: Double,
        uniqueID: Int,
        isPayer: Boolean): Boolean
    {
        for (s1 in store)
        {
            if (s1["id"] as Int == uniqueID)
            {
                return false
            }
            if (isPayer && s1["payer"] as Boolean)
            {
                return false
            }
        }

        val dictionary: Dictionary<String?, Any> = Hashtable(0x5)
        dictionary.put("name", personName)
        dictionary.put("amount", amount)
        dictionary.put("id", uniqueID)
        dictionary.put("payer", isPayer)
        return store.add(dictionary)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): AddNewRecordRecyclerViewHolder
    {
        val inflatedView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.add_new_record_recyclerviewitem, parent, false)
        return AddNewRecordRecyclerViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: AddNewRecordRecyclerViewHolder,
        position: Int)
    {
        val data = store[position]
        holder.setAmount(data["amount"] as Double)
        holder.setImageVisibility(data["payer"] as Boolean)
        holder.setName(data["name"] as String)
    }

    override fun getItemCount(): Int
    {
        return (store.size)
    }
}