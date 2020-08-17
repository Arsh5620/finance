package org.arshdeep.financemanagement

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.arshdeep.financemanagement.kotlin.AddNewRecordRecyclerViewAdapter
import org.arshdeep.financemanagement.kotlin.Database
import java.util.*

class AddNewRecordActivity : AppCompatActivity()
{
    lateinit var adapter: AddNewRecordRecyclerViewAdapter
    private val ADD_USER_REQUEST_CODE = 3

    val USER_INFO_NAME = 0
    val USER_INFO_ID = 1

    val TAG: String = this.javaClass.toString()

    private var hasPayer = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_record)

        val recyclerView =
            findViewById<View>(R.id.add_new_record_recyclerview_main) as RecyclerView

        adapter = AddNewRecordRecyclerViewAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /*
     * Pass in values:
     * Activity: AddGroupMember
     * Parameters: "alreadyHasPayer" {user has already selected someone who will be paying}
     *
     * Dictionary<String, Object>
     *     keys: "name", "amount", "id", "payer"
     *     All lower cases
     *
     * ADD_USER_REQUEST_CODE
     * Returned values;
     *  userInfo    [0](string)=getUserName()
     *              [1](string)=getUserInformation()
     *              [2](int)=user.getUserID()
     *  userAmount -The amount that is personal spending for the user, and can't be counted towards
     *              the group split
     *  userPaid   -Did the user paid for the entire group, only one person can!
     */
    fun addContributor(view: View)
    {
        val intent =
            Intent(this@AddNewRecordActivity, AddMemberActivity::class.java)
        intent.putExtra("paid", hasPayer)
        val billAmount =
            (findViewById<View>(R.id.add_new_record_value_spent) as EditText).text
                .toString()
        if (billAmount.isNotEmpty())
        {
            var allowedMaximum = billAmount.toDouble()
            for (p in adapter.store)
            {
                allowedMaximum -= p["amount"] as Double
            }
            intent.putExtra("maximum", allowedMaximum)
        }
        startActivityForResult(intent, ADD_USER_REQUEST_CODE)
    }

    fun submitClicked(view: View?)
    {
        val connection = Database.factory()!!
        try
        {
            connection.startTransaction()
            val list: List<Dictionary<String?, Any>> = adapter.store
            var payerID = -1
            var totalSpending = 0.0
            for (item in list)
            {
                if (item["payer"] as Boolean)
                {
                    payerID = item["id"] as Int
                }

                totalSpending += item["amount"] as Double
            }

            val dialog = AlertDialog.Builder(this@AddNewRecordActivity)
                .setPositiveButton("OK", null)

            if (payerID == -1)
            {
                dialog.setTitle(getString(R.string.add_new_record_alert_no_payer_title))
                    .setMessage(getString(R.string.add_new_record_alert_no_payer_text))
                    .show()
                return
            }

            val totalGroupSpending =
                (findViewById<View>(R.id.add_new_record_value_spent) as EditText)
                    .text.toString().toDouble()

            if (totalGroupSpending <= 0 || totalGroupSpending < totalSpending)
            {
                dialog.setTitle(getString(R.string.add_new_record_alert_amount_conflict_title))
                    .setMessage(getString(R.string.add_new_record_alert_amount_conflict_text))
                    .show()
                return
            }

            val groupId: Long =
                connection.addPaymentRecord(totalGroupSpending, payerID)

            for (item in this.adapter.store)
            {
                if (!connection.addUsersOwingMoney(item["id"] as Int
                            , groupId.toInt()
                            , item["amount"] as Double)
                )
                {
                    throw java.lang.Exception(
                        "Unknown issue occured in AddNewRecordActivity.kt, " +
                                "somehow addUsersOwningMoney returned an incorrect value")
                }
            }
            connection.setTransactionSuccessful()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        catch (e: Exception)
        {
            Log.d(TAG, "onClick: " + e.message)
        }
        finally
        {
            connection.endTransaction()
        }
    }

    override fun onActivityResult(requestCode: Int,
        resultCode: Int,
        data: Intent?)
    {
        when (requestCode)
        {
            ADD_USER_REQUEST_CODE ->
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    /**
                     * Return [0] = name(string), [1] = uniqueId (int)
                     */
                    val userInfo =
                        data!!.getStringArrayExtra("userInfo")

                    val amount =
                        data.getDoubleExtra("userAmount", 0.0)
                    val paid =
                        data.getBooleanExtra("userPaid", false)

                    if (adapter.addItem(userInfo[USER_INFO_NAME]
                                , amount
                                , userInfo[USER_INFO_ID].toInt(), paid))
                    {
                        adapter.notifyItemInserted(adapter.itemCount)
                        if (paid)
                        {
                            hasPayer = true
                        }
                    }
                    else
                    {
//                        AlertDialog.Builder(this)
//                            .setTitle(resources.getText(
//                                R.string.add_new_record_user_already_exists_title
//                            ))
//                            .setMessage(resources.getText(
//                                R.string.add_new_record_user_already_exists_body
//                            ))
//                            .setPositiveButton("OK", null).show()

                        // TODO
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}