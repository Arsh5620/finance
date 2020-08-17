package org.arshdeep.financemanagement.kotlin

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.arshdeep.financemanagement.R
import java.util.*
import kotlin.math.roundToInt

class MainActivityRecyclerViewHolder(private val view: View,
    clickListener: MainActivityRecyclerViewClickListener?) :
    RecyclerView.ViewHolder(view)
{
    private val dateTextView: TextView =
        view.findViewById<View>(R.id.recyclerview_value_date) as TextView
    private val nameTextView: TextView =
        view.findViewById<View>(R.id.recyclerview_value_payer) as TextView
    private val amountTextView: TextView =
        view.findViewById<View>(R.id.recyclerview_value_amount) as TextView
    private val peopleTextView: TextView =
        view.findViewById<View>(R.id.recyclerview_value_entrants) as TextView
    private var defaultDrawable: Drawable? = null

    private val index = 0
    fun setDate(fileDate: Long): MainActivityRecyclerViewHolder
    {
        val date = Date(fileDate)
        val dateString = String.format(
            "%d/%d/%d"
            , date.month, date.date, date.year + 1900)
        dateTextView.text = dateString
        return this
    }

    fun setName(name: String?): MainActivityRecyclerViewHolder
    {
        nameTextView.text = name
        return this
    }

    fun setAmount(amount: Double): MainActivityRecyclerViewHolder
    {
        amountTextView.text = "$${amount.roundToInt()}"
        return this
    }

    fun setPeopleCount(count: Int): MainActivityRecyclerViewHolder
    {
        peopleTextView.text = "${count}u"
        return this
    }

    fun setBackground(state: Boolean): MainActivityRecyclerViewHolder
    {
        if (!state)
        {
            view.background = defaultDrawable
        }
        else
        {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                view.background =
                    view.resources.getDrawable(R.drawable.background_selected,
                        null)
            }
            else
            {
                view.background =
                    view.resources.getDrawable(R.drawable.background_selected);
            }
        }
        return this
    }

    init
    {
        if (clickListener != null)
        {
            view.setOnLongClickListener(clickListener)
            view.setOnClickListener(clickListener)
        }
        defaultDrawable = view.background
    }
}