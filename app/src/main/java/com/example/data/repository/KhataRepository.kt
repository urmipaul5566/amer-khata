package com.example.data.repository

import com.example.data.dao.BusinessProfileDao
import com.example.data.dao.CashbookDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.TransactionDao
import com.example.data.entity.BusinessProfile
import com.example.data.entity.CashbookEntry
import com.example.data.entity.Customer
import com.example.data.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class KhataRepository(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao,
    private val cashbookDao: CashbookDao,
    private val profileDao: BusinessProfileDao
) {
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allCashbookEntries: Flow<List<CashbookEntry>> = cashbookDao.getAllCashbookEntries()
    val businessProfile: Flow<BusinessProfile?> = profileDao.getProfile()

    fun getCustomerById(id: Long): Flow<Customer?> = customerDao.getCustomerById(id)
    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)
    fun getTransactionsForCustomer(customerId: Long): Flow<List<TransactionEntity>> = transactionDao.getTransactionsForCustomer(customerId)

    suspend fun addCustomer(customer: Customer): Long {
        return customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        transactionDao.deleteTransactionsForCustomer(customer.id)
        customerDao.deleteCustomer(customer)
    }

    suspend fun addTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
        recalculateCustomerBalance(transaction.customerId)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
        recalculateCustomerBalance(transaction.customerId)
    }

    private suspend fun recalculateCustomerBalance(customerId: Long) {
        val txs = transactionDao.getTransactionsForCustomer(customerId).firstOrNull() ?: emptyList()
        var balance = 0.0
        txs.forEach { tx ->
            if (tx.type == "PABO") {
                balance += tx.amount
            } else if (tx.type == "DIBO") {
                balance -= tx.amount
            }
        }
        val now = System.currentTimeMillis()
        customerDao.updateCustomerBalance(customerId, balance, now)
    }

    suspend fun addCashbookEntry(entry: CashbookEntry) {
        cashbookDao.insertCashbookEntry(entry)
    }

    suspend fun saveBusinessProfile(profile: BusinessProfile) {
        profileDao.saveProfile(profile)
    }
}
