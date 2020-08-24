package org.arshdeep.financemanagement.kotlin

import org.arshdeep.financemanagement.objects.MortgageFrequency
import org.arshdeep.financemanagement.objects.MortgagePayment
import org.arshdeep.financemanagement.objects.MortgageType
import kotlin.math.pow

class MortgageCalculate(amount: Double,
    amortization: Int,
    interest: Double,
    frequency: MortgageFrequency,
    type: MortgageType,
    interestTerm: Double)
{
    var amount: Double = 0.0
    var amortization: Int = 0
    var interest: Double = 0.0
    var frequency: Double = 0.0
    var type: MortgageType
    var interestTerm: Double = 0.0
    var periods: Double = 0.0
    var periodAmount: Double = 0.0
    private var annuityFactor: Double = 0.0
    var totalInterestPaid = 0.0

    init
    {
        this.amount = amount
        this.amortization = amortization
        this.interest = interest
        this.frequency = when (frequency)
        {
            MortgageFrequency.FREQUENCY_WEEKLY ->
            {
                52.0
            }
            MortgageFrequency.FREQUENCY_MONTHLY ->
            {
                12.0
            }
            MortgageFrequency.FREQUENCY_BIWEEKLY ->
            {
                26.0
            }
        }
        this.interestTerm = interestTerm

        this.type = type
        this.periods = this.amortization * this.frequency

        when (type)
        {
            MortgageType.MORTGAGE_TYPE_FIXED ->
            {
                this.interest = (this.interest / 100)
                val modifiedInterest =
                    ((1 + (this.interest / 2)) * (1 + (this.interest / 2)))
                this.interest = modifiedInterest.pow(1 / this.frequency) - 1
            }
            MortgageType.MORTGAGE_TYPE_VARIABLE ->
            {
                this.interest = (this.interest / 100) / this.frequency
            }
        }

        //https://wiki.treasurers.org/wiki/Annuity_factor
        this.annuityFactor =
            (1 - (1 + this.interest).pow(-periods)) / this.interest

        //https://wiki.treasurers.org/wiki/Discount_factor
        this.periodAmount = this.amount / annuityFactor
        this.totalInterestPaid =
            (this.periodAmount * this.periods) - this.amount
    }

    //http://www.yorku.ca/amarshal/mortgage.htm
    fun getPayments(): List<MortgagePayment>
    {
        var loanRemaining = this.amount
        var interestAccrued = 0.0
        var totalEquity = 0.0
        val listOfPayments = ArrayList<MortgagePayment>()

        var i = 0
        while (i < periods)
        {
            val interestThisPeriod =
                loanRemaining * this.interest / (this.frequency * 100)
            val principalThisPeriod = this.periodAmount - interestThisPeriod
            totalEquity += principalThisPeriod
            loanRemaining -= principalThisPeriod
            interestAccrued += interestThisPeriod

            listOfPayments.add(MortgagePayment(i,
                totalEquity,
                loanRemaining,
                this.periodAmount,
                principalThisPeriod,
                interestThisPeriod,
                interestAccrued))
            i++
        }

        return listOfPayments
    }

    override fun toString(): String
    {
        return "Mortgage Amount: $${this.amount}\n" +
                "Payment per period: $${
                    String.format("%.2f",
                        this.periodAmount)
                }\n" +
                "Number of periods: ${this.periods}\n" +
                "Number of Years: ${this.amortization}\n" +
                "Total interest paid: ${
                    String.format("%.2f",
                        this.totalInterestPaid)
                }"
    }
}