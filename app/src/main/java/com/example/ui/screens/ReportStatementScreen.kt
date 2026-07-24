package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Customer
import com.example.data.entity.TransactionEntity
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceVariantLight
import com.example.ui.theme.TaliGreen
import com.example.ui.theme.TaliGreenLight
import com.example.ui.theme.TaliNavy
import com.example.ui.theme.TaliRed
import com.example.ui.theme.TaliRedLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.KhataUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportStatementScreen(
    customer: Customer?,
    allCustomers: List<Customer>,
    transactions: List<TransactionEntity>,
    onSelectCustomer: (Long) -> Unit,
    onUpdateTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    onBackClick: (() -> Unit)? = null,
    businessName: String = "আমার খাতা",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Customer Dropdown State
    var showCustomerDropdown by remember { mutableStateOf(false) }

    // Transaction Edit/Delete Modal States
    var selectedTxForAction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showEditTxDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Edit Transaction Fields
    var editAmountText by remember { mutableStateOf("") }
    var editNoteText by remember { mutableStateOf("") }
    var editTxType by remember { mutableStateOf("PABO") }
    var editDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Date Picker for Edit Dialog
    val calendar = Calendar.getInstance().apply { timeInMillis = editDateMillis }
    val datePickerDialog = remember(context) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                editDateMillis = newCal.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Calculate Running Balance Map from Oldest to Newest
    val sortedOldestFirst = remember(transactions) { transactions.sortedBy { it.timestamp } }
    val runningBalanceMap = remember(sortedOldestFirst) {
        var cumulative = 0.0
        val map = mutableMapOf<Long, Double>()
        sortedOldestFirst.forEach { tx ->
            if (tx.type == "PABO") {
                cumulative += tx.amount
            } else {
                cumulative -= tx.amount
            }
            map[tx.id] = cumulative
        }
        map
    }

    // Calculate Totals
    val totalDilam = remember(transactions) { transactions.filter { it.type == "PABO" }.sumOf { it.amount } }
    val totalPelam = remember(transactions) { transactions.filter { it.type == "DIBO" }.sumOf { it.amount } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "রিপোর্ট",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TaliNavy
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TaliNavy
                            )
                        }
                    }
                },
                actions = {
                    // Download / Export Report File Action
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                val reportText = buildString {
                                    append("📄 $businessName - স্টেটমেন্ট রিপোর্ট\n")
                                    customer?.let {
                                        append("গ্রাহক: ${it.name} (${it.phone})\n")
                                        append("বর্তমান স্থিতি: ${KhataUtils.formatCurrency(it.totalBalance)}\n")
                                    }
                                    append("\n-----------------------------------\n")
                                    append("তারিখ | বিবরণ | দিলাম | পেলাম\n")
                                    append("-----------------------------------\n")
                                    transactions.forEach { tx ->
                                        val dateStr = KhataUtils.formatBengaliFullDate(tx.timestamp)
                                        val dilamVal = if (tx.type == "PABO") KhataUtils.formatNumberOnly(tx.amount) else "-"
                                        val pelamVal = if (tx.type == "DIBO") KhataUtils.formatNumberOnly(tx.amount) else "-"
                                        val note = tx.note.ifBlank { "-" }
                                        append("$dateStr ($note): দিলাম $dilamVal, পেলাম $pelamVal\n")
                                    }
                                    append("-----------------------------------\n")
                                    append("মোট: দিলাম ${KhataUtils.formatCurrency(totalDilam)}, পেলাম ${KhataUtils.formatCurrency(totalPelam)}\n")
                                    append("\nধন্যবাদ (আমার খাতা)।")
                                }

                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "রিপোর্ট শেয়ার / ডাউনলোড করুন"))
                            }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .testTag("btn_download_report")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Report",
                            tint = TaliNavy,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "ডাউনলোড",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaliNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            // Sticky Totals Bar at Bottom (Exact design from screenshot)
            Surface(
                color = PureWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    HorizontalDivider(color = BorderLight, thickness = 1.dp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = "মোট",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TaliNavy,
                            modifier = Modifier.weight(1.8f)
                        )

                        Text(
                            text = "৳ ${KhataUtils.formatNumberOnly(totalDilam)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TaliRed,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.0f)
                        )

                        Text(
                            text = "৳ ${KhataUtils.formatNumberOnly(totalPelam)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TaliGreen,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1.0f)
                        )
                    }
                }
            }
        },
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureWhite)
        ) {
            // Header Section: Customer Avatar, Name & Current Balance
            Surface(
                color = PureWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Avatar Circle (Matching light circle in screenshot)
                    val currentCustomer = customer ?: allCustomers.firstOrNull()
                    val customerName = currentCustomer?.name ?: "গ্রাহক নির্বাচন করুন"
                    val avatarLetter = customerName.take(1).uppercase()

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFDF4EA)) // Light beige/warm background as in screenshot
                            .border(1.dp, Color(0xFFE2D6C5), CircleShape)
                    ) {
                        Text(
                            text = avatarLetter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFFC5954B)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { showCustomerDropdown = true }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = customerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = TaliNavy
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Customer",
                                    tint = TaliNavy
                                )
                            }

                            DropdownMenu(
                                expanded = showCustomerDropdown,
                                onDismissRequest = { showCustomerDropdown = false }
                            ) {
                                allCustomers.forEach { cust ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                Text(cust.name, fontWeight = FontWeight.Medium)
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = KhataUtils.formatCurrency(cust.totalBalance),
                                                    color = if (cust.totalBalance >= 0) TaliGreen else TaliRed,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        },
                                        onClick = {
                                            onSelectCustomer(cust.id)
                                            showCustomerDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        val bal = currentCustomer?.totalBalance ?: 0.0
                        val balLabel = when {
                            bal > 0 -> "পাবো ৳ ${KhataUtils.formatNumberOnly(bal)}"
                            bal < 0 -> "দিবো ৳ ${KhataUtils.formatNumberOnly(kotlin.math.abs(bal))}"
                            else -> "পাবো ৳ ০"
                        }
                        val balColor = when {
                            bal > 0 -> TaliRed
                            bal < 0 -> TaliGreen
                            else -> TaliRed
                        }

                        Text(
                            text = balLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = balColor
                        )
                    }
                }
            }

            // Table Header Bar (Exact columns: "লেনদেনের বিবরণ", "দিলাম", "পেলাম")
            Surface(
                color = Color(0xFFF5F5F5), // Light grey header bar as in screenshot
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "লেনদেনের বিবরণ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.weight(1.8f)
                    )

                    Text(
                        text = "দিলাম",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TaliRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1.0f)
                    )

                    Text(
                        text = "পেলাম",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TaliGreen,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1.0f)
                    )
                }
            }

            // Table Body List
            if (transactions.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Text(
                        text = "এই কাস্টমারের কোনো লেনদেন রিপোর্ট নেই",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(transactions, key = { it.id }) { tx ->
                        val runningBal = runningBalanceMap[tx.id] ?: 0.0

                        ReportTransactionRowItem(
                            transaction = tx,
                            runningBalance = runningBal,
                            onClick = {
                                selectedTxForAction = tx
                                editAmountText = if (tx.amount % 1.0 == 0.0) tx.amount.toInt().toString() else tx.amount.toString()
                                editNoteText = tx.note
                                editTxType = tx.type
                                editDateMillis = tx.timestamp
                                showEditTxDialog = true
                            }
                        )
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    }
                }
            }
        }
    }

    // --- ACTION DIALOGS ---

    // Edit Transaction Dialog
    if (showEditTxDialog && selectedTxForAction != null) {
        val currentTx = selectedTxForAction!!

        AlertDialog(
            onDismissRequest = { showEditTxDialog = false },
            title = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("লেনদেন এডিট করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    IconButton(onClick = {
                        showEditTxDialog = false
                        showDeleteConfirmDialog = true
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TaliRed)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Type Selection Buttons (দিলাম vs পেলাম)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val isPabo = editTxType == "PABO"
                        Surface(
                            color = if (isPabo) TaliRedLight else PureWhite,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .border(1.dp, if (isPabo) TaliRed else BorderLight, RoundedCornerShape(8.dp))
                                .clickable { editTxType = "PABO" }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("দিলাম (বাকি)", fontWeight = FontWeight.Bold, color = if (isPabo) TaliRed else TextPrimary, fontSize = 13.sp)
                            }
                        }

                        val isDibo = editTxType == "DIBO"
                        Surface(
                            color = if (isDibo) TaliGreenLight else PureWhite,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .border(1.dp, if (isDibo) TaliGreen else BorderLight, RoundedCornerShape(8.dp))
                                .clickable { editTxType = "DIBO" }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("পেলাম (জমা)", fontWeight = FontWeight.Bold, color = if (isDibo) TaliGreen else TextPrimary, fontSize = 13.sp)
                            }
                        }
                    }

                    // Amount
                    OutlinedTextField(
                        value = editAmountText,
                        onValueChange = { editAmountText = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("টাকার পরিমাণ (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Description / Note
                    OutlinedTextField(
                        value = editNoteText,
                        onValueChange = { editNoteText = it },
                        label = { Text("বিবরণ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Date Chip
                    Surface(
                        color = SurfaceVariantLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = TaliNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "তারিখ: ${KhataUtils.formatBengaliFullDate(editDateMillis)}",
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amountVal = editAmountText.toDoubleOrNull() ?: 0.0
                        if (amountVal > 0) {
                            val updatedTx = currentTx.copy(
                                amount = amountVal,
                                note = editNoteText.trim(),
                                type = editTxType,
                                timestamp = editDateMillis
                            )
                            onUpdateTransaction(updatedTx)
                            showEditTxDialog = false
                            Toast.makeText(context, "লেনদেন আপডেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "সঠিক পরিমাণ দিন", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaliNavy)
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditTxDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog && selectedTxForAction != null) {
        val currentTx = selectedTxForAction!!

        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("লেনদেন ডিলিট করুন") },
            text = { Text("আপনি কি নিশ্চিতভাবে এই ${KhataUtils.formatCurrency(currentTx.amount)}-এর লেনদেনটি মুছে ফেলতে চান?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTransaction(currentTx)
                        showDeleteConfirmDialog = false
                        Toast.makeText(context, "লেনদেন মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("হ্যাঁ, ডিলিট করুন", color = TaliRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun ReportTransactionRowItem(
    transaction: TransactionEntity,
    runningBalance: Double,
    onClick: () -> Unit
) {
    val isDilam = transaction.type == "PABO" // Gave / Sales

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("report_row_${transaction.id}")
    ) {
        // Column 1: Transaction Details (Date, Time, Note, Running Balance Pill)
        Column(modifier = Modifier.weight(1.8f)) {
            // Full Bengali Date
            Text(
                text = KhataUtils.formatBengaliFullDate(transaction.timestamp),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )

            // Bengali Time
            Text(
                text = KhataUtils.formatBengaliTime(transaction.timestamp),
                fontSize = 12.sp,
                color = Color(0xFF888888)
            )

            // Note
            val noteText = transaction.note.ifBlank { "-" }
            Text(
                text = noteText,
                fontSize = 12.sp,
                color = Color(0xFF777777)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Running Balance Badge Pill (e.g. "পাবো ১,০০০")
            val balanceTagText = when {
                runningBalance > 0 -> "পাবো ${KhataUtils.formatNumberOnly(runningBalance)}"
                runningBalance < 0 -> "দিবো ${KhataUtils.formatNumberOnly(kotlin.math.abs(runningBalance))}"
                else -> "পাবো 0"
            }

            Surface(
                color = Color(0xFFEEEEEE), // Neutral gray pill as in screenshot
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = balanceTagText,
                    fontSize = 11.sp,
                    color = Color(0xFF555555),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Column 2: "দিলাম" Amount (Gave / Red text)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1.0f)
        ) {
            if (isDilam) {
                Text(
                    text = KhataUtils.formatNumberOnly(transaction.amount),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TaliRed
                )
            } else {
                Text(
                    text = "-",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF999999)
                )
            }
        }

        // Column 3: "পেলাম" Amount (Received / Green text)
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.weight(1.0f)
        ) {
            if (!isDilam) {
                Text(
                    text = KhataUtils.formatNumberOnly(transaction.amount),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TaliGreen
                )
            } else {
                Text(
                    text = "-",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}
