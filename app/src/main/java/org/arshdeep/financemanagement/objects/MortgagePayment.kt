package org.arshdeep.financemanagement.objects

class MortgagePayment(var paymentNumber: Int,
    var equity: Double,
    var debt: Double,
    var payment: Double,
    var principalPaid: Double,
    var interest: Double,
    var interestAccrued: Double)
{
    override fun toString(): String
    {
        return "For payment number $paymentNumber, " +
                "total equity build up is $equity, " +
                "total debt remaining is $debt, " +
                "total payment made this interval is $payment, " +
                "(interest: $interest, " +
                "principal: $principalPaid), " +
                "total interest accrued so far is $interestAccrued"
    }
}