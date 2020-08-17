package org.arshdeep.financemanagement

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.arshdeep.financemanagement.kotlin.*
import org.arshdeep.financemanagement.objects.RecordInformation

class MainActivity : AppCompatActivity()
{
    private lateinit var adapter: MainActivityRecyclerViewAdapter
    private var selectionOngoing = false
    private var selectionCounter = 0
    private val ADD_OBJECT_ACTIVITY_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        Database.factory(this)

        val recyclerView: RecyclerView =
            findViewById<View>(R.id.recyclerview_main) as RecyclerView
        adapter = MainActivityRecyclerViewAdapter(null, this);

        val helper = object : ItemTouchHelper.SimpleCallback(0
            , ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onMove(recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean
            {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                direction: Int)
            {
                val position: Int = viewHolder.adapterPosition
                val info: RecordInformation = adapter.getItem(position)
                Toast.makeText(this@MainActivity, "Deleting no longer allowed.",
                    Toast.LENGTH_SHORT).show()
                if (selectionOngoing && info.getSelected())
                {
                    selectionCounter--
                    if (selectionCounter == 0)
                    {
                        clearRecyclerViewSelection()
                    }
                }
                adapter.removeItem(position)
                adapter.notifyItemRemoved(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(helper)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val clickListener: MainActivityRecyclerViewClickListener =
            object :
                MainActivityRecyclerViewClickListener(recyclerView, adapter)
            {
                override fun onLongClick(view: View?,
                    info: RecordInformation?,
                    position: Int)
                {
                    if (!selectionOngoing)
                    {
                        selectionOngoing = true
                        info!!.setSelected(!info.getSelected())
                        adapter.notifyNow(-1)
                        selectionCounter++
                    }
                    else
                    {
                        clearRecyclerViewSelection()
                    }
                }

                override fun onClick(view: View?,
                    info: RecordInformation?,
                    position: Int)
                {
                    if (selectionOngoing && selectionCounter > 0)
                    {
                        val selected: Boolean = !info!!.getSelected()
                        info.setSelected(selected)
                        adapter.notifyNow(position)
                        if (selected)
                        {
                            selectionCounter++
                        }
                        else
                        {
                            selectionCounter--
                        }
                        if (selectionCounter == 0)
                        {
                            clearRecyclerViewSelection()
                        }
                    }
                    else
                    {
                        Toast.makeText(this@MainActivity,
                            "TODO: Not Implemented",
                            Toast.LENGTH_LONG).show()
//                        val intent = Intent(this@MainActivity,
//                            ViewRecordInfoActivity::class.java)
//                        intent.putExtra("recordId",
//                            adapter.getItem(position).getRowId())
//                        startActivity(intent)
                    }
                }
            }

        adapter.setClickListener(clickListener)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        refreshRecyclerViewContents()
    }

    private fun clearRecyclerViewSelection()
    {
        adapter.clearSelection()
        selectionOngoing = false
        adapter.notifyNow(-1)
        selectionCounter = 0
    }

    private fun refreshRecyclerViewContents()
    {
        selectionOngoing = false
        adapter.clearItems()
        for (recordInformation in Database.factory()!!.getAllPurchases()!!)
        {
            adapter.addItem(recordInformation)
        }
        adapter.notifyNow(-1)
    }

    override fun onActivityResult(requestCode: Int,
        resultCode: Int,
        data: Intent?)
    {
        when (requestCode)
        {
            ADD_OBJECT_ACTIVITY_CODE ->
            {
                if (resultCode == Activity.RESULT_OK) refreshRecyclerViewContents()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onKeyDown(keyCode: Int,
        event: KeyEvent?): Boolean
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (selectionOngoing)
            {
                clearRecyclerViewSelection()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    fun calculateFinalOwing(view: View?)
    {
        val c = Expenses(
            adapter.getRecordInformationList(),
            selectionOngoing)
        val allPayments: ArrayList<Payments> = c.getPayments()
        var finalString = String()
        for (payment in allPayments)
        {
            if (payment.toString().isNotEmpty())
            {
                finalString += payment.toString() + "\n\n"
            }
        }
        if (finalString.isEmpty())
        {
            finalString = "Nobody owes Nobody Nothing."
        }

        AlertDialog.Builder(this).setTitle("Total Owing")
            .setMessage(finalString)
            .setPositiveButton("OK", null).show()
    }

    fun addNewTransaction(view: View?)
    {
        val intent = Intent(this@MainActivity, AddNewRecordActivity::class.java)
        startActivityForResult(intent, ADD_OBJECT_ACTIVITY_CODE)
    }
}