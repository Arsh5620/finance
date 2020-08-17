package org.arshdeep.financemanagement.kotlin

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.arshdeep.financemanagement.R

class AddNewRecordRecyclerViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)
{
    private val userNameTextView: TextView =
        itemView.findViewById<View>(R.id.add_new_record_recyclerview_name) as TextView
    private val personalSpendingTextView: TextView =
        itemView.findViewById<View>(R.id.add_new_record_recyclerview_amount) as TextView
    private val paidByImage: ImageView =
        itemView.findViewById<View>(R.id.add_new_record_recyclerview_payer_image) as ImageView

    fun setName(name: String?)
    {
        userNameTextView.text = name
    }

    fun setAmount(amount: Double)
    {
        personalSpendingTextView.text =
            if (amount > 0)
            {
                String.format("$%.3f", amount)
            }
            else
            {
                "None"
            }
    }

    fun setImageVisibility(show: Boolean)
    {
        paidByImage.visibility =
            if (show)
            {
                View.VISIBLE
            }
            else
            {
                View.INVISIBLE
            }
    }
}