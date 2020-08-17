package org.arshdeep.financemanagement.kotlin

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import org.arshdeep.financemanagement.R
import org.arshdeep.financemanagement.objects.RecordInformation
import java.util.*
import kotlin.collections.ArrayList

public class MainActivityRecyclerViewAdapter(recordInformation: Array<RecordInformation?>?
    , private var context: Activity) :
    RecyclerView.Adapter<MainActivityRecyclerViewHolder>()
{
    private val recordInformationList = ArrayList<RecordInformation>()
    public fun getRecordInformationList(): ArrayList<RecordInformation>
    {
        return (this.recordInformationList)
    }
    private var clickListener: MainActivityRecyclerViewClickListener? = null

    fun setClickListener(clickListener: MainActivityRecyclerViewClickListener?)
    {
        this.clickListener = clickListener
    }

    fun addItem(@NonNull item: RecordInformation)
    {
        recordInformationList.add(item)
    }

    fun getItem(position: Int): RecordInformation
    {
        return recordInformationList[position]
    }

    fun notifyNow(record: Int)
    {
        if (record == -1)
        {
            this.notifyDataSetChanged()
        }
        else
        {
            this.notifyItemChanged(record)
        }
    }

    fun clearItems()
    {
        recordInformationList.clear()
    }

    fun removeItem(index: Int)
    {
        recordInformationList.removeAt(index)
    }

    fun clearSelection()
    {
        for (i in recordInformationList)
        {
            i.setSelected(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): MainActivityRecyclerViewHolder
    {
        val inflatedView: View = LayoutInflater
            .from(context)
            .inflate(R.layout.home_recyclerview, parent, false)
        return MainActivityRecyclerViewHolder(inflatedView, clickListener)
    }

    override fun onBindViewHolder(holder: MainActivityRecyclerViewHolder,
        position: Int)
    {
        val info: RecordInformation = recordInformationList[position]
        holder.setDate(info.getDate())
            .setName(info.getName())
            .setAmount(info.getAmount())
            .setPeopleCount(info.getPeople())
            .setBackground(info.getSelected())
    }

    override fun getItemCount(): Int
    {
        return recordInformationList.size
    }

    init
    {
        if (recordInformation != null)
        {
            Collections.addAll<RecordInformation>(
                recordInformationList, *recordInformation)
        }
    }
}