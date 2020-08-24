package org.arshdeep.financemanagement

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.arshdeep.financemanagement.kotlin.MortgageCalculate
import org.arshdeep.financemanagement.objects.MortgageFrequency
import org.arshdeep.financemanagement.objects.MortgageType

class MortgageActivity : AppCompatActivity()
{
    val TAG = this::class.java.name
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mortgage)
    }

    fun buttonMortgageCalculateOnClick(view: View)
    {
        try
        {

            val mortgageAmount =
                findViewById<EditText>(R.id.editTextMortgageAmount).text.toString()
                    .toDouble()
            val amortization =
                findViewById<EditText>(R.id.editTextAmortizationPeriod).text.toString()
                    .toInt()
            val interestRate =
                findViewById<EditText>(R.id.editTextInterestAmount).text.toString()
                    .toDouble()
            val interestTerm =
                findViewById<EditText>(R.id.editTextInterestTerm).text.toString()
                    .toDouble()
            val paymentFrequency =
                findViewById<Spinner>(R.id.editTextPaymentFrequency).selectedItemPosition
            val interestType =
                findViewById<Spinner>(R.id.editTextInterestType).selectedItemPosition

            val mortgage = MortgageCalculate(mortgageAmount,
                amortization,
                interestRate,
                MortgageFrequency.values()[paymentFrequency],
                MortgageType.values()[interestType],
                interestTerm)
            AlertDialog.Builder(this@MortgageActivity)
                .setTitle("Mortgage Calculator").setMessage(mortgage.toString())
                .setPositiveButton("Ok", null).show()

        } catch (e: NumberFormatException)
        {
            Toast.makeText(this@MortgageActivity,
                "Cannot execute mortgage calculator, Empty values found.",
                Toast.LENGTH_LONG).show()
            return
        }
    }
}