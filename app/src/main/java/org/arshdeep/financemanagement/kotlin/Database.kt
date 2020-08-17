package org.arshdeep.financemanagement.kotlin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.arshdeep.financemanagement.objects.RecordInformation
import org.arshdeep.financemanagement.objects.UserInformation
import java.util.*
import kotlin.collections.ArrayList

class Database private constructor(context: Context?,
    filename: String) : SQLiteOpenHelper(context, filename, null, 1)
{
    private val TAG = this.toString()

    companion object
    {
        private var connection: Database? = null
        private var database: SQLiteDatabase? = null

        fun factory(context: Context?): Database?
        {
            if (connection == null || !connection!!.writableDatabase.isOpen)
            {
                connection = Database(context, "data.sqlite3")
                this.database = connection!!.writableDatabase
            }
            return connection
        }

        fun factory(): Database?
        {
            return factory(null)
        }
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        val queries =
            arrayOf("CREATE TABLE Attachment(AttachmentID INTEGER PRIMARY KEY" +
                    ", Type INT NOT NULL" +
                    ", Data TEXT NOT NULL);",
                "CREATE TABLE Users(UserID INTEGER PRIMARY KEY" +
                        ", UserName TEXT NOT NULL);",
                "CREATE TABLE Payment(PaymentID INTEGER PRIMARY KEY" +
                        ", PaymentDate NUMERIC NOT NULL" +
                        ", TotalBill REAL NOT NULL" +
                        ", AttachmentID INT" +
                        ", IsCompleted INT NOT NULL" +
                        ", PayerUserID INT NOT NULL" +
                        ", BillID INT" +
                        ", Description TEXT" +
                        ", FOREIGN KEY(AttachmentID) REFERENCES Attachment(AttachmentID)" +
                        ", FOREIGN KEY(PayerUserID) REFERENCES Users(UserID));",
                "CREATE TABLE MoneyOwes(MoneyOwesID INTEGER PRIMARY KEY" +
                        ", UserID INT" +
                        ", PaymentID INT" +
                        ", Amount REAL NOT NULL" +
                        ", FOREIGN KEY(UserID) REFERENCES Users(UserID)" +
                        ", FOREIGN KEY(PaymentID) REFERENCES Payment(PaymentID)" +
                        ");")
        for (sql in queries)
        {
            db.execSQL(sql)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int)
    {
        Log.d(TAG, "Update for database is not handled internally.")
    }

    fun getUsers(): ArrayList<UserInformation>
    {
        val dictionary: ArrayList<UserInformation> = ArrayList()

        val cursor = database!!.query(
                "Users" ,
                arrayOf("UserID", "UserName"),
                null ,
                null ,
                null ,
                null ,
                null)

        while (cursor.moveToNext())
        {
            dictionary.add(
                UserInformation(
                    cursor.getInt(0)
                    , cursor.getString(1)
                ))
        }
        return dictionary
    }

    fun addUser(name: String?): Boolean
    {
        val values = ContentValues()
        values.put("UserName", name)
        val result = database!!.insert("Users", null, values)
        return (result > 0)
    }

    fun addPaymentRecord(totalBillAmount: Double,
        payerID: Int): Long
    {
        val values = ContentValues()
        values.put("PaymentDate", java.lang.Long.valueOf(Date().time))
        values.put("TotalBill", totalBillAmount)
        values.put("IsCompleted", 0)
        values.put("PayerUserID", payerID)
        values.put("Description", "In the name of God, Amen.")
        return database!!.insert("Payment", null, values)
    }

    fun addUsersOwingMoney(userID: Int,
        groupID: Int,
        amount: Double): Boolean
    {
        val values = ContentValues()
        values.put("UserID", userID)
        values.put("PaymentID", groupID)
        values.put("Amount", amount)
        return database!!.insert("MoneyOwes", null, values) > 0
    }

    fun startTransaction()
    {
        database!!.beginTransaction()
    }

    fun setTransactionSuccessful()
    {
        database!!.setTransactionSuccessful()
    }

    fun endTransaction()
    {
        database!!.endTransaction()
    }

    fun getAllPurchases(): List<RecordInformation>?
    {
        val sql =
            """
            SELECT Payment.rowId as "rowId",
            Users.UserName as "name",
            Payment.PaymentDate as "date",
            Payment.TotalBill as "amount",
            (SELECT COUNT(*) 
                FROM MoneyOwes 
                WHERE MoneyOwes.PaymentID = Payment.RowId)
            AS "people"
            FROM Payment 
            INNER JOIN Users
            ON Users.RowId = Payment.PayerUserID;
            """

        val cursor = database!!.rawQuery(sql, null)
        val lists: MutableList<RecordInformation> = ArrayList()
        while (cursor.moveToNext())
        {
            var record  = RecordInformation(
                cursor.getString(cursor.getColumnIndex("name"))
                , cursor.getLong(cursor.getColumnIndex("date"))
                , cursor.getDouble(cursor.getColumnIndex("amount"))
                , cursor.getInt(cursor.getColumnIndex("people"))
                , cursor.getInt(cursor.getColumnIndex("rowId")))

            lists.add(record)
        }
        return lists
    }

    fun getRecordInformation(recordId: Int): RecordInformation?
    {
        val sql =
            """SELECT Users.UserName as "name"
                |, IFNULL(Bill.StoreName, '') as "store"
                |, GroupPayment.PaymentDate as "date"
                |, GroupPayment.TotalBill as "amount" 
                | FROM GroupPayment 
                | LEFT OUTER JOIN Bill ON Bill.rowid = GroupPayment.BillId 
                | INNER JOIN Users ON Users.rowID = GroupPayment.PayerUserID 
                | WHERE GroupPayment.rowId = ?;""".trimMargin();

        val selectionArgs = arrayOf<String>(recordId.toString())
        val cursor = database!!.rawQuery(sql, selectionArgs);
        cursor.use {
            if (cursor.moveToNext())
            {
                return RecordInformation(
                    cursor.getString(cursor.getColumnIndex("name"))
                    , cursor.getLong(cursor.getColumnIndex("date"))
                    , cursor.getDouble(cursor.getColumnIndex("amount"))
                    , -1
                    , recordId
                )
            }
        }
        return null
    }

//
//    fun deletePaymentRecord(paymentID: Int): Boolean
//    {
//        try
//        {
//            startTransaction()
//            val deleteSql = arrayOf(
//                """
//                    delete from Attachment where Attachment.rowid = (select AttachmentID from GroupPayment where GroupPayment.rowid = ?);
//
//                    """.trimIndent(),
//                "    delete from Bill where Bill.rowid = (select BillID from GroupPayment where GroupPayment.rowid = ?);\n",
//                "    delete from MoneyOwes where MoneyOwes.GroupPaymentID = ?;\n",
//                "    delete from GroupPayment where GroupPayment.rowid = ?;"
//            )
//            for (s in deleteSql)
//            {
//                val statement = database!!.compileStatement(s)
//                statement.bindLong(1, paymentID.toLong())
//                statement.executeUpdateDelete()
//            }
//            setTransactionSuccessful()
//            return true
//        } catch (e: Exception)
//        {
//            Log.d(TAG, "deletePaymentRecord: " + e.message)
//        } finally
//        {
//            endTransaction()
//        }
//        return false
//    }

    private fun computeExpensesPrivate(sql: String): ArrayList<Expenses.ExpenseMetrics>
    {
        val cursor = database!!.rawQuery(sql, null)
        val listCalc = ArrayList<Expenses.ExpenseMetrics>()
        while (cursor.moveToNext())
        {
            val expenseMetrics  = Expenses.ExpenseMetrics()
            expenseMetrics.groupContribution =
                cursor.getDouble(cursor.getColumnIndex("TotalBill"))
            expenseMetrics.userID =
                cursor.getInt(cursor.getColumnIndex("PayerUserID"))
            val rowId = cursor.getInt(cursor.getColumnIndex("PaymentID"))
            val userCursor = database!!.rawQuery(
                "SELECT MoneyOwes.UserID, amount FROM MoneyOwes where MoneyOwes.PaymentID = ?",
                Array(1){rowId.toString()})

            expenseMetrics.usersID = ArrayList<Int>()
            expenseMetrics.userMoney = Hashtable<Int, Double>()

            while (userCursor.moveToNext())
            {
                val userId =
                    userCursor.getInt(userCursor.getColumnIndex("UserID"))
                expenseMetrics.usersID!!.add(userId)
                expenseMetrics.userMoney!!.put(userId
                    , userCursor.getDouble(userCursor.getColumnIndex("Amount")))
            }
            listCalc.add(expenseMetrics)
        }
        return listCalc
    }

    fun getAllCalculations(): List<Expenses.ExpenseMetrics>
    {
        val sql = "SELECT totalbill, payeruserid, PaymentID FROM Payment"
        return computeExpensesPrivate(sql)
    }

    fun getSomeCalculations(ids: List<Int>): List<Expenses.ExpenseMetrics>
    {
        var inStatement = "("
        for (i in ids.indices) inStatement += (if (i == 0) "" else ", ") + ids[i].toString()
        inStatement += ")"
        val sql =
            "SELECT totalbill, payeruserid, PaymentID FROM Payment WHERE Payment.PaymentID in $inStatement"
        return computeExpensesPrivate(sql)
    }
}