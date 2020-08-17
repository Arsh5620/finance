package org.arshdeep.financemanagement.objects

class UserInformation(
    private var userID: Int,
    private var userName: String?)
{
    private var extras: Any? = null

    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun getUserID(): Int {
        return userID
    }

    override fun toString(): String {
        return userName.toString()
    }

    fun getExtras(): Any? {
        return extras
    }

    fun setExtras(extras: Any?) {
        this.extras = extras
    }
}