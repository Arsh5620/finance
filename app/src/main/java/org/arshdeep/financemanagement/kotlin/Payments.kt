package org.arshdeep.financemanagement.kotlin

import android.annotation.SuppressLint
import org.arshdeep.financemanagement.objects.UserInformation
import kotlin.math.abs

class Payments(private var firstPersonId: Int, private var secondPersonId: Int
    , private var firstPerson: UserInformation
    , private var secondPerson: UserInformation)
{
    private var firstPersonAmount: kotlin.Double = 0.0
    private var secondPersonAmount: kotlin.Double = 0.0

    fun getAmount(): Double
    {
        return (firstPersonAmount - secondPersonAmount)
    }

    fun getAbsAmount(): Double
    {
        return (abs(firstPersonAmount - secondPersonAmount))
    }

    fun getPrimaryUser(): UserInformation
    {
        return this.firstPerson
    }

    fun getSecondUser(): UserInformation
    {
        return this.secondPerson
    }

    fun getPersonOwed(): UserInformation
    {
        return if(this.getAmount() >= 0)
        {
            getPrimaryUser()
        }
        else
        {
            getSecondUser()
        }
    }

    fun getPersonOwing(): UserInformation
    {
        return if(this.getAmount() >= 0)
        {
            getSecondUser()
        }
        else
        {
            getPrimaryUser();
        }
    }

    fun addExpense(firstPersonId: Int,
        secondPersonId: Int,
        amount: Double): Boolean
    {
        return if (this.firstPersonId == firstPersonId
                && this.secondPersonId == secondPersonId)
        {
            //The first person owes the next money
            firstPersonAmount += amount
            true
        }
        else if (this.secondPersonId == firstPersonId
                && this.firstPersonId == secondPersonId)
        {
            //The second person owes the first person money
            secondPersonAmount += amount
            true
        }
        else throw IllegalArgumentException()
    }

    fun equals(forward: Int, backward: Int): Boolean
    {
        return ((firstPersonId == forward
                && this.secondPersonId == backward)
                || (this.secondPersonId == forward
                && firstPersonId == backward))
    }

    companion object
    {
        fun findPayment(allPayments: ArrayList<Payments>,
            forward: Int,
            backward: Int): Payments?
        {
            for (payment in allPayments)
            {
                if (payment.equals(forward,backward))
                {
                    return payment
                }
            }
            return null
        }
    }

    @SuppressLint("DefaultLocale")
    override fun toString(): String
    {
        return if (getAmount() != 0.0)
        {
            java.lang.String.format("User \'%s\' owes \'%s\' \n\t\t $%.2f",
                if (getAmount() > 0) secondPerson.getUserName() else firstPerson.getUserName(),
                if (getAmount() > 0) firstPerson.getUserName() else secondPerson.getUserName(),
                abs(getAmount())
            )
        }
        else String()
    }

    fun isValid(): Boolean
    {
        return getAmount() != 0.0
    }
}