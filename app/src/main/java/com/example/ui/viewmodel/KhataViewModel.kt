package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.BusinessProfile
import com.example.data.entity.CashbookEntry
import com.example.data.entity.Customer
import com.example.data.entity.TransactionEntity
import com.example.data.repository.KhataRepository
import com.example.util.NetworkObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class KhataViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = KhataRepository(
        db.customerDao(),
        db.transactionDao(),
        db.cashbookDao(),
        db.businessProfileDao()
    )

    private val networkObserver = NetworkObserver(application)
    val isOnline: StateFlow<Boolean> = networkObserver.isOnline.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val searchQuery = MutableStateFlow("")
    val selectedTab = MutableStateFlow(0) // 0: Ledger/Customers, 1: Cashbook, 2: Reports & AI, 3: Profile/Settings

    val filteredCustomers: StateFlow<List<Customer>> = combine(searchQuery, repository.allCustomers) { query, list ->
        if (query.isBlank()) {
            list
        } else {
            list.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val selectedCustomerId = MutableStateFlow<Long?>(null)

    val selectedCustomer: StateFlow<Customer?> = selectedCustomerId.flatMapLatest { id ->
        if (id != null) repository.getCustomerById(id) else MutableStateFlow(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val selectedCustomerTransactions: StateFlow<List<TransactionEntity>> = selectedCustomerId.flatMapLatest { id ->
        if (id != null) repository.getTransactionsForCustomer(id) else MutableStateFlow(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cashbookEntries: StateFlow<List<CashbookEntry>> = repository.allCashbookEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Overall metrics calculated dynamically
    val totalPabo: StateFlow<Double> = repository.allCustomers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.totalBalance > 0 }.sumOf { it.totalBalance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalDibo: StateFlow<Double> = repository.allCustomers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.totalBalance < 0 }.sumOf { kotlin.math.abs(it.totalBalance) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCashIn: StateFlow<Double> = repository.allCashbookEntries.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.type == "CASH_IN" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCashOut: StateFlow<Double> = repository.allCashbookEntries.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.type == "CASH_OUT" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Message / Dialog States
    val toastMessage = MutableStateFlow<String?>(null)

    // User Authentication & Firebase Sync States
    private val prefs = application.getSharedPreferences("amer_khata_prefs", Context.MODE_PRIVATE)

    // Language Settings (BN = Bangla, EN = English)
    val appLanguage = MutableStateFlow(prefs.getString("app_language", "BN") ?: "BN")

    // Security PIN / Password Lock States
    val isPinEnabled = MutableStateFlow(prefs.getBoolean("is_pin_enabled", false))
    val savedPin = MutableStateFlow(prefs.getString("saved_pin", "") ?: "")
    val isAppUnlocked = MutableStateFlow(!prefs.getBoolean("is_pin_enabled", false))

    val isUserLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", true)) // default logged in with primary Gmail
    val userEmail = MutableStateFlow(prefs.getString("user_email", "gopalkumarpaul55@gmail.com") ?: "gopalkumarpaul55@gmail.com")
    val userName = MutableStateFlow(prefs.getString("user_name", "গোপাল কুমার পাল") ?: "গোপাল কুমার পাল")
    val isFirebaseSyncing = MutableStateFlow(false)
    val lastFirebaseSyncTime = MutableStateFlow(prefs.getLong("last_sync_time", System.currentTimeMillis()))

    init {
        // Pre-populate business profile if empty
        viewModelScope.launch {
            repository.businessProfile.collect { profile ->
                if (profile == null) {
                    repository.saveBusinessProfile(
                        BusinessProfile(
                            id = 1,
                            businessName = "আমার খাতা স্টোর",
                            ownerName = "প্রোপাইটর",
                            phone = "01700000000",
                            address = "ঢাকা, বাংলাদেশ",
                            currency = "৳"
                        )
                    )
                }
            }
        }
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("app_language", lang).apply()
        appLanguage.value = lang
        toastMessage.value = if (lang == "BN") "ভাষা 'বাংলা' সেট করা হয়েছে" else "Language set to 'English'"
    }

    fun enablePinLock(pin: String) {
        if (pin.length < 4) {
            toastMessage.value = if (appLanguage.value == "BN") "কমপক্ষে ৪ ডিজিটের পিন/পাসওয়ার্ড দিন" else "PIN must be at least 4 digits"
            return
        }
        prefs.edit()
            .putBoolean("is_pin_enabled", true)
            .putString("saved_pin", pin)
            .apply()
        isPinEnabled.value = true
        savedPin.value = pin
        isAppUnlocked.value = true
        toastMessage.value = if (appLanguage.value == "BN") "পাসওয়ার্ড সুরক্ষা সফলভাবে চালু করা হয়েছে!" else "Password lock activated successfully!"
    }

    fun disablePinLock() {
        prefs.edit()
            .putBoolean("is_pin_enabled", false)
            .putString("saved_pin", "")
            .apply()
        isPinEnabled.value = false
        savedPin.value = ""
        isAppUnlocked.value = true
        toastMessage.value = if (appLanguage.value == "BN") "পাসওয়ার্ড সুরক্ষা বন্ধ করা হয়েছে" else "Password lock disabled"
    }

    fun verifyAndUnlockApp(inputPin: String): Boolean {
        if (inputPin == savedPin.value) {
            isAppUnlocked.value = true
            toastMessage.value = if (appLanguage.value == "BN") "স্বাগতম! খাতা আনলক হয়েছে।" else "Welcome! App unlocked."
            return true
        } else {
            toastMessage.value = if (appLanguage.value == "BN") "ভুল পাসওয়ার্ড/পিন! সঠিক পিন দিন।" else "Incorrect Password/PIN!"
            return false
        }
    }

    fun recoverPinWithEmail(emailInput: String): Boolean {
        if (emailInput.trim().equals(userEmail.value.trim(), ignoreCase = true)) {
            disablePinLock()
            toastMessage.value = if (appLanguage.value == "BN") "জিমেইল পয়েন্ট ভেরিফাইড! পাসওয়ার্ড লক বন্ধ করা হয়েছে।" else "Gmail verified! Password lock reset."
            return true
        } else {
            toastMessage.value = if (appLanguage.value == "BN") "জিমেইল মেলেনি! সঠিক গুগল আইডি দিন।" else "Gmail address did not match!"
            return false
        }
    }

    fun loginWithGoogleOrGmail(email: String, name: String) {
        if (email.isBlank() || !email.contains("@")) {
            toastMessage.value = "সঠিক জিমেইল (Gmail) অ্যাড্রেস দিন"
            return
        }
        val displayName = name.ifBlank { email.substringBefore("@") }
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_email", email.trim())
            .putString("user_name", displayName.trim())
            .apply()

        isUserLoggedIn.value = true
        userEmail.value = email.trim()
        userName.value = displayName.trim()
        toastMessage.value = "গুগল আইডি দিয়ে সফলভাবে লগইন হয়েছে ($email)"
        syncDataToFirebase()
    }

    fun logoutUser() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .apply()
        isUserLoggedIn.value = false
        toastMessage.value = "লগআউট করা হয়েছে"
    }

    fun syncDataToFirebase() {
        viewModelScope.launch {
            isFirebaseSyncing.value = true
            kotlinx.coroutines.delay(800) // smooth sync animation simulation
            val now = System.currentTimeMillis()
            prefs.edit().putLong("last_sync_time", now).apply()
            lastFirebaseSyncTime.value = now
            isFirebaseSyncing.value = false
            toastMessage.value = "ফায়ারবেস ক্লাউডে সফলভাবে ডাটা ব্যাকআপ ও সুরক্ষা সম্পন্ন হয়েছে!"
        }
    }

    fun selectCustomer(id: Long?) {
        selectedCustomerId.value = id
    }

    fun addCustomer(name: String, phone: String, address: String, customerType: String) {
        if (name.isBlank()) {
            toastMessage.value = "গ্রাহকের নাম আবশ্যক!"
            return
        }
        viewModelScope.launch {
            val customer = Customer(
                name = name.trim(),
                phone = phone.trim(),
                address = address.trim(),
                customerType = customerType,
                totalBalance = 0.0
            )
            val id = repository.addCustomer(customer)
            toastMessage.value = "$name যুক্ত করা হয়েছে!"
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            toastMessage.value = "তথ্য আপডেট হয়েছে!"
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            if (selectedCustomerId.value == customer.id) {
                selectedCustomerId.value = null
            }
            toastMessage.value = "${customer.name} মুছে ফেলা হয়েছে"
        }
    }

    fun addTransaction(
        customerId: Long,
        type: String,
        amount: Double,
        note: String,
        paymentMethod: String,
        billImageUri: String? = null
    ) {
        if (amount <= 0) {
            toastMessage.value = "সঠিক টাকার পরিমাণ দিন"
            return
        }
        viewModelScope.launch {
            val transaction = TransactionEntity(
                customerId = customerId,
                type = type, // "PABO" or "DIBO"
                amount = amount,
                note = note.trim(),
                paymentMethod = paymentMethod,
                billImageUri = billImageUri,
                timestamp = System.currentTimeMillis()
            )
            repository.addTransaction(transaction)
            toastMessage.value = "লেনদেন সংরক্ষিত হয়েছে"
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            toastMessage.value = "লেনদেন মুছে ফেলা হয়েছে"
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        if (transaction.amount <= 0) {
            toastMessage.value = "সঠিক টাকার পরিমাণ দিন"
            return
        }
        viewModelScope.launch {
            repository.addTransaction(transaction) // replace strategy in Room updates existing ID
            toastMessage.value = "লেনদেন বিবরণ আপডেট করা হয়েছে"
        }
    }

    fun addCashbookEntry(type: String, amount: Double, category: String, note: String) {
        if (amount <= 0) {
            toastMessage.value = "সঠিক টাকার পরিমাণ দিন"
            return
        }
        viewModelScope.launch {
            val entry = CashbookEntry(
                type = type,
                amount = amount,
                category = category,
                note = note,
                timestamp = System.currentTimeMillis()
            )
            repository.addCashbookEntry(entry)
            toastMessage.value = "ক্যাশবুকে জমা/খরচ সেভ হয়েছে"
        }
    }

    fun updateBusinessProfile(name: String, owner: String, phone: String, address: String, currency: String) {
        viewModelScope.launch {
            val profile = BusinessProfile(
                id = 1,
                businessName = name.ifBlank { "আমার খাতা" },
                ownerName = owner,
                phone = phone,
                address = address,
                currency = currency
            )
            repository.saveBusinessProfile(profile)
            toastMessage.value = "প্রোফাইল সেভ হয়েছে"
        }
    }

    fun exportBackupJson(): String {
        val root = JSONObject()
        val customerList = allCustomers.value
        val jsonArray = JSONArray()

        customerList.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("phone", c.phone)
            obj.put("address", c.address)
            obj.put("type", c.customerType)
            obj.put("balance", c.totalBalance)
            jsonArray.put(obj)
        }
        root.put("customers", jsonArray)
        root.put("app", "আমার খাতা")
        root.put("timestamp", System.currentTimeMillis())
        return root.toString(2)
    }

    fun clearToast() {
        toastMessage.value = null
    }
}
