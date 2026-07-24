package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.HeaderBar
import com.example.ui.components.SummaryCardRow
import com.example.ui.screens.CashbookScreen
import com.example.ui.screens.CustomerDetailScreen
import com.example.ui.screens.CustomerListScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.ReportsAndAiScreen
import com.example.ui.theme.AmerKhataTheme
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.KhataViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AmerKhataTheme {
                AmerKhataApp()
            }
        }
    }
}

@Composable
fun AmerKhataApp(viewModel: KhataViewModel = viewModel()) {
    val context = LocalContext.current

    val isOnline by viewModel.isOnline.collectAsState()
    val customers by viewModel.filteredCustomers.collectAsState()
    val allCustomers by viewModel.allCustomers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedCustomer by viewModel.selectedCustomer.collectAsState()
    val selectedTransactions by viewModel.selectedCustomerTransactions.collectAsState()
    val cashbookEntries by viewModel.cashbookEntries.collectAsState()
    val businessProfile by viewModel.businessProfile.collectAsState()

    val totalPabo by viewModel.totalPabo.collectAsState()
    val totalDibo by viewModel.totalDibo.collectAsState()
    val totalCashIn by viewModel.totalCashIn.collectAsState()
    val totalCashOut by viewModel.totalCashOut.collectAsState()

    val toastMsg by viewModel.toastMessage.collectAsState()

    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val isFirebaseSyncing by viewModel.isFirebaseSyncing.collectAsState()
    val lastFirebaseSyncTime by viewModel.lastFirebaseSyncTime.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val isAppUnlocked by viewModel.isAppUnlocked.collectAsState()

    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Seed sample data if database is brand new
    LaunchedEffect(allCustomers) {
        if (allCustomers.isEmpty()) {
            viewModel.addCustomer("আরিফুর রহমান", "01711223344", "মিরপুর, ঢাকা", "CUSTOMER")
            viewModel.addCustomer("রহিম পাইকারি বিডি", "01899887766", "চকবাজার, ঢাকা", "SUPPLIER")
            viewModel.addCustomer("কামাল হোসেন", "01900112233", "ধানমন্ডি, ঢাকা", "CUSTOMER")
        }
    }

    val shopName = businessProfile?.businessName ?: "আমার খাতা স্টোর"
    val ownerName = businessProfile?.ownerName ?: "প্রোপাইটর"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        if (isPinEnabled && !isAppUnlocked) {
            com.example.ui.screens.AppLockScreen(
                shopName = shopName,
                userEmail = userEmail,
                appLanguage = appLanguage,
                onUnlock = { pinInput ->
                    viewModel.verifyAndUnlockApp(pinInput)
                },
                onRecoverPin = { emailInput ->
                    viewModel.recoverPinWithEmail(emailInput)
                }
            )
        } else if (selectedCustomer != null) {
            // Detailed Customer Ledger Screen View
            CustomerDetailScreen(
                customer = selectedCustomer!!,
                transactions = selectedTransactions,
                onBackClick = { viewModel.selectCustomer(null) },
                onAddTransaction = { type, amount, note, paymentMethod, billImageUri ->
                    viewModel.addTransaction(selectedCustomer!!.id, type, amount, note, paymentMethod, billImageUri)
                },
                onDeleteTransaction = { tx -> viewModel.deleteTransaction(tx) },
                onUpdateTransaction = { tx -> viewModel.updateTransaction(tx) },
                onDeleteCustomer = { c -> viewModel.deleteCustomer(c) },
                onUpdateCustomer = { updated -> viewModel.updateCustomer(updated) },
                allCustomers = allCustomers,
                onSelectCustomer = { id -> viewModel.selectCustomer(id) },
                businessName = shopName
            )
        } else {
            // Main App Navigation View
            Scaffold(
                topBar = {
                    Column {
                        HeaderBar(
                            businessName = shopName,
                            ownerName = ownerName,
                            isOnline = isOnline,
                            appLanguage = appLanguage,
                            onLanguageToggle = {
                                val nextLang = if (appLanguage == "BN") "EN" else "BN"
                                viewModel.setLanguage(nextLang)
                            },
                            onSearchClick = {
                                viewModel.selectedTab.value = 0
                            }
                        )
                        if (selectedTab == 0) {
                            SummaryCardRow(
                                totalPabo = totalPabo,
                                totalDibo = totalDibo,
                                currencySymbol = businessProfile?.currency ?: "৳",
                                appLanguage = appLanguage
                            )
                        }
                    }
                },
                bottomBar = {
                    BottomNavBar(
                        selectedTab = selectedTab,
                        appLanguage = appLanguage,
                        onTabSelected = { tabIndex ->
                            viewModel.selectCustomer(null)
                            viewModel.selectedTab.value = tabIndex
                        }
                    )
                },
                containerColor = PureWhite
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(PureWhite)
                ) {
                    AnimatedContent(targetState = selectedTab, label = "TabSwitch") { tab ->
                        when (tab) {
                            0 -> CustomerListScreen(
                                customers = customers,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.searchQuery.value = it },
                                onCustomerClick = { id -> viewModel.selectCustomer(id) },
                                onAddCustomer = { name, phone, address, type ->
                                    viewModel.addCustomer(name, phone, address, type)
                                },
                                businessName = shopName
                            )
                            1 -> CashbookScreen(
                                cashbookEntries = cashbookEntries,
                                totalCashIn = totalCashIn,
                                totalCashOut = totalCashOut,
                                onAddEntry = { type, amount, category, note ->
                                    viewModel.addCashbookEntry(type, amount, category, note)
                                }
                            )
                            2 -> ReportsAndAiScreen(
                                customers = allCustomers,
                                transactions = selectedTransactions,
                                selectedCustomer = selectedCustomer,
                                onSelectCustomer = { id -> viewModel.selectCustomer(id) },
                                onUpdateTransaction = { tx -> viewModel.updateTransaction(tx) },
                                onDeleteTransaction = { tx -> viewModel.deleteTransaction(tx) },
                                businessName = shopName
                            )
                            3 -> ProfileSettingsScreen(
                                profile = businessProfile,
                                isLoggedIn = isUserLoggedIn,
                                userEmail = userEmail,
                                userName = userName,
                                isFirebaseSyncing = isFirebaseSyncing,
                                lastSyncTime = lastFirebaseSyncTime,
                                appLanguage = appLanguage,
                                isPinEnabled = isPinEnabled,
                                onLanguageChange = { lang ->
                                    viewModel.setLanguage(lang)
                                },
                                onEnablePinLock = { pin ->
                                    viewModel.enablePinLock(pin)
                                },
                                onDisablePinLock = {
                                    viewModel.disablePinLock()
                                },
                                onSaveProfile = { name, owner, phone, address, currency ->
                                    viewModel.updateBusinessProfile(name, owner, phone, address, currency)
                                },
                                onGoogleLogin = { email, name ->
                                    viewModel.loginWithGoogleOrGmail(email, name)
                                },
                                onLogout = {
                                    viewModel.logoutUser()
                                },
                                onFirebaseSync = {
                                    viewModel.syncDataToFirebase()
                                },
                                onExportBackupJson = {
                                    viewModel.exportBackupJson()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
