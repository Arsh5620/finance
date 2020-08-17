package org.arshdeep.financemanagement.kotlin

import org.arshdeep.financemanagement.objects.RecordInformation
import org.arshdeep.financemanagement.objects.UserInformation
import java.util.*
import kotlin.collections.ArrayList

class Expenses(infoList: List<RecordInformation>,
    selection: Boolean)
{
    private var payments: ArrayList<Payments> = ArrayList()
    public fun getPayments(): ArrayList<Payments>
    {
        return this.payments
    }

    class ExpenseMetrics
    {
        var groupContribution = 0.0
        var userID = 0
        var usersID: ArrayList<Int>? = null
        var userMoney: Dictionary<Int, Double>? = null
    }

    var connection: Database? = Database.factory(null)
    private val TAG = this.toString()

    private fun fillList(payments: MutableList<Payments>,
        user: ArrayList<UserInformation>)
    {
        while (user.isNotEmpty())
        {
            val u1: UserInformation = user.removeAt(0)
            for (u2 in user)
            {
                payments.add(Payments(u1.getUserID(),
                    u2.getUserID(),
                    u1,
                    u2))
            }
        }
    }

    override fun toString(): String
    {
        return super.toString()
    }

    init
    {
        //Here, we have to determine what to do, if there is no id selected.
        var calculation: List<ExpenseMetrics>

        calculation = if (selection)
        {
            val ids: MutableList<Int> = ArrayList()
            for (i in infoList)
            {
                if (i.getSelected())
                {
                    ids.add(i.getRowId())
                }
            }
            connection!!.getSomeCalculations(ids)
        }
        else
        {
            connection!!.getAllCalculations()
        }

        val allIds: ArrayList<UserInformation> = connection!!.getUsers()
        fillList(payments, allIds)

        for (c in calculation)
        {
            val owner = c.userID
            var contrib = c.groupContribution
            for (m in c.usersID!!)
            {
                contrib -= c.userMoney!![m]
            }

            for (m in c.usersID!!)
            {
                if (m != owner)
                {
                    val payment = Payments.findPayment(payments, owner,m)
                    payment!!.addExpense(owner, m, c.userMoney!![m] + contrib / c.usersID!!.size)
                }
            }
        }
    }
}