package org.arshdeep.financemanagement.objects

class RecordInformation(
    private var name: String?,
    private var date: Long,
    private var amount: Double,
    private var people: Int,
    private var rowId: Int)
{
    private var isSelected = false

    init {
        this.isSelected = false
    }

    fun setSelected(state: Boolean) {
        isSelected = state
    }

    fun getSelected(): Boolean {
        return isSelected
    }

    fun getName(): String? {
        return name
    }

    fun getDate(): Long {
        return date
    }

    fun getAmount(): Double {
        return amount
    }

    fun getPeople(): Int {
        return people
    }

    fun getRowId(): Int {
        return rowId
    }
}