package org.arshdeep.financemanagement

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.arshdeep.financemanagement.kotlin.Database
import org.arshdeep.financemanagement.objects.UserInformation

class AddMemberActivity : AppCompatActivity()
{
    var connection: Database? = null
    var adapter: ArrayAdapter<UserInformation>? = null
    var spinner: Spinner? = null
    var maximumAmount = 0.0

    val TAG: String = this.javaClass.toString()

    private fun populateUsers()
    {
        val users: List<UserInformation> = connection!!.getUsers()
        adapter!!.clear()
        for (user in users)
        {
            adapter!!.add(user)
        }
        adapter!!.notifyDataSetChanged()
    }

    fun addNewUser(view: View?)
    {
        val intent = Intent(this@AddMemberActivity, AddNewUser::class.java)
        startActivityForResult(intent, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member_transaction)

        connection = Database.factory()

        adapter = ArrayAdapter<UserInformation>(this, R.layout.spinner_record)
        spinner = findViewById<View>(R.id.add_member_name_value) as Spinner
        adapter!!.setDropDownViewResource(R.layout.spinner_record)
        spinner!!.adapter = adapter

        populateUsers()

        if (intent.getBooleanExtra("paid", false))
        {
            (findViewById<View>(R.id.add_member_paid_header)).visibility =
                View.GONE
            (findViewById<View>(R.id.add_member_paid_value)).visibility =
                View.GONE
        }

        maximumAmount = intent.getDoubleExtra("maximum", -1.0)
    }

    fun returnButtonClick(view: View?)
    {
        if (spinner!!.selectedItemId != AdapterView.INVALID_ROW_ID)
        {
            val user: UserInformation = spinner!!.selectedItem as UserInformation
            intent.putExtra("userInfo"
                , arrayOf(
                    user.getUserName()
                    , user.getUserID().toString()
                ))

            Log.d(TAG, "Username: ${user.getUserName()}, ${user.getUserID()}")
            intent.putExtra("userPaid",
                (findViewById<View>(R.id.add_member_paid_value) as Switch).isChecked)

            try
            {
                val number = (findViewById<View>(R.id.add_member_amount_value) as EditText)
                        .text.toString()
                var parseDouble = 0.0
                if (number.isNotEmpty())
                {
                    parseDouble = number.toDouble()
                }

                if (maximumAmount == -1.0 || number.isEmpty() || parseDouble <= maximumAmount)
                {
                    intent.putExtra("userAmount", parseDouble)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else
                {
                    AlertDialog.Builder(this)
                        .setTitle(resources.getText(
                            R.string.add_new_record_alert_amount_conflict_title
                        ))
                        .setMessage(resources.getText(
                            R.string.add_new_record_alert_amount_conflict_text
                        ))
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            catch (e: NumberFormatException)
            {
                Log.d(TAG,"returnButtonClick: " + e.stackTrace)
            }
        }
        else
        {
            Toast.makeText(applicationContext,
                "Did not selected the member to add to the transaction, try again!",
                Toast.LENGTH_LONG).show()
        }
    }
}