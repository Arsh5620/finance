package org.arshdeep.financemanagement.kotlin

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import org.arshdeep.financemanagement.objects.RecordInformation

public abstract class MainActivityRecyclerViewClickListener(private var recyclerView: RecyclerView,
    private var adapter: MainActivityRecyclerViewAdapter) :
    View.OnLongClickListener, View.OnClickListener
{
    override fun onClick(v: View)
    {
        val position: Int = recyclerView.getChildLayoutPosition(v)
        onClick(v, adapter.getItem(position), position)
    }

    override fun onLongClick(v: View): Boolean
    {
        val position: Int = recyclerView.getChildLayoutPosition(v)
        onLongClick(v, adapter.getItem(position), position)
        return true
    }

    abstract fun onLongClick(view: View?,
        info: RecordInformation?,
        position: Int)

    abstract fun onClick(view: View?,
        info: RecordInformation?,
        position: Int)
}