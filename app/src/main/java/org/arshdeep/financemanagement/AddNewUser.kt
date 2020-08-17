package org.arshdeep.financemanagement

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.arshdeep.financemanagement.kotlin.Database

class AddNewUser : AppCompatActivity()
{
    private var mUserName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_user)

        mUserName =
            findViewById<View>(R.id.add_new_user_name_value) as TextView
    }

    fun addNewUser(view: View?)
    {
        val userName = mUserName!!.text.toString()

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@AddNewUser)
            .setTitle(getString(R.string.add_new_user_missing_text))
            .setPositiveButton("OK", null)

        if (userName.isEmpty())
        {
            alertDialog.setMessage(getString(R.string.add_new_user_cancel_transaction_text)).show()
            return
        }

        var result = if (Database.factory(applicationContext)!!.addUser(userName))
        {
            "User added"
        }
        else
        {
            "User cannot be added"
        }

        alertDialog
            .setMessage(result)
            .setTitle("")
            .setPositiveButton("OK"
            ) { _, _ -> finish() }
            .show()
    }
}