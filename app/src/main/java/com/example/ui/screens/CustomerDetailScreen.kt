package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
fun CustomerDetailScreen(
    customer: Customer,
    transactions: List<TransactionEntity>,
    onBackClick: () -> Unit,
    onAddTransaction: (type: String, amount: Double, note: String, paymentMethod: String, billImageUri: String?) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    onUpdateTransaction: (TransactionEntity) -> Unit = {},
    onDeleteCustomer: (Customer) -> Unit,
    onUpdateCustomer: (Customer) -> Unit = {},
    allCustomers: List<Customer> = emptyList(),
    onSelectCustomer: (Long) -> Unit = {},
    businessName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Photo Picker Launcher & View States
    var attachedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var viewFullPhotoUri by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedPhotoUri = uri
            Toast.makeText(context, "মেমো/ভাউচারের ছবি সংযুক্ত করা হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }

    // Three-dot Overflow Menu State
    var showMenu by remember { mutableStateOf(false) }

    // Dialog States
    var showEditDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // --- TRANSACTION EDIT & DELETE STATES ---
    var selectedTxForAction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showEditTxDialog by remember { mutableStateOf(false) }
    var showDeleteTxConfirmDialog by remember { mutableStateOf(false) }

    // Edit Transaction Fields State
    var editTxAmount by remember { mutableStateOf("") }
    var editTxNote by remember { mutableStateOf("") }
    var editTxType by remember { mutableStateOf("PABO") }
    var editTxPaymentMethod by remember { mutableStateOf("ক্যাশ") }
    var editTxDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Date picker for editing transaction
    val txCalendar = Calendar.getInstance().apply { timeInMillis = editTxDateMillis }
    val txDatePickerDialog = remember(context) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                editTxDateMillis = newCal.timeInMillis
            },
            txCalendar.get(Calendar.YEAR),
            txCalendar.get(Calendar.MONTH),
            txCalendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // In-Screen Add Transaction Form States (matching the screenshot)
    var selectedTxType by remember { mutableStateOf("PABO") } // PABO (দিলাম/বেচা) or DIBO (পেলাম/জমা)
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var taliMessageEnabled by remember { mutableStateOf(true) }
    var hasPhotoAttached by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("CASH") }

    // Date Picker Dialog Setup
    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
    val datePickerDialog = remember(context) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                selectedDateMillis = newCal.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Last transaction timestamp for subheader
    val lastTxTimestamp = transactions.firstOrNull()?.timestamp ?: 0L

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Customer Avatar Circle with Initials
                        val initials = if (customer.name.length >= 2) customer.name.take(2) else customer.name.take(1)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TaliNavy)
                        ) {
                            Text(
                                text = initials,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = customer.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = TaliNavy
                            )
                            if (customer.phone.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Call",
                                        tint = TaliGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = customer.phone,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TaliNavy)
                    }
                },
                actions = {
                    if (customer.phone.isNotBlank()) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Call, contentDescription = "Call Customer", tint = TaliGreen)
                        }

                        IconButton(onClick = {
                            val smsText = KhataUtils.generateSmsReminder(customer.name, customer.totalBalance, businessName, customer.phone)
                            val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${customer.phone}")).apply {
                                putExtra("sms_body", smsText)
                            }
                            context.startActivity(sendIntent)
                        }) {
                            Icon(Icons.Default.Sms, contentDescription = "Send SMS", tint = TaliNavy)
                        }
                    }

                    // Three-dot Overflow Menu Button
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("btn_three_dot_menu")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = TaliNavy
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // 1. Report (রিপোর্ট)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Assessment,
                                            contentDescription = null,
                                            tint = TaliNavy,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("রিপোর্ট", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showReportDialog = true
                                }
                            )

                            // 2. Edit (এডিট)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = TaliNavy,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("এডিট", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showEditDialog = true
                                }
                            )

                            // 3. Delete (ডিলিট)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = TaliRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("ডিলিট", fontWeight = FontWeight.Medium, color = TaliRed, fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showDeleteConfirmDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureWhite)
        ) {
            // Sub-header Row (Balance & Last Transaction Time) - Matching screenshot
            val balance = customer.totalBalance
            Surface(
                color = SurfaceVariantLight.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    val balanceLabel = when {
                        balance > 0 -> "পাবো ${KhataUtils.formatCurrency(balance)}"
                        balance < 0 -> "দিবো ${KhataUtils.formatCurrency(kotlin.math.abs(balance))}"
                        else -> "পাবো ৳ ০.০০"
                    }
                    val balanceColor = when {
                        balance > 0 -> TaliGreen
                        balance < 0 -> TaliRed
                        else -> TaliRed
                    }

                    Text(
                        text = balanceLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = balanceColor
                    )

                    Text(
                        text = KhataUtils.getTimeAgoBengali(lastTxTimestamp),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // Quick Messaging Row (SMS & WhatsApp Reminders)
            if (customer.phone.isNotBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    // SMS Button
                    Surface(
                        color = TaliNavy.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val smsText = KhataUtils.generateSmsReminder(customer.name, customer.totalBalance, businessName, customer.phone)
                                val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${customer.phone}")).apply {
                                    putExtra("sms_body", smsText)
                                }
                                context.startActivity(sendIntent)
                            }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Sms, contentDescription = null, tint = TaliNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SMS মেসেজ পাঠান", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TaliNavy)
                        }
                    }

                    // Share WhatsApp Button
                    Surface(
                        color = TaliGreenLight,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val text = KhataUtils.generateSmsReminder(customer.name, customer.totalBalance, businessName, customer.phone)
                                val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=${customer.phone}&text=${Uri.encode(text)}"))
                                try {
                                    context.startActivity(waIntent)
                                } catch (e: Exception) {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, text)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "হিসাব শেয়ার করুন"))
                                }
                            }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = TaliGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("হোয়াটসঅ্যাপ/শেয়ার", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TaliGreen)
                        }
                    }
                }
            }

            // In-Screen Add Transaction Form Card (Exactly as pictured in screenshot)
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Direct Side-by-Side Money Entry Boxes for "দিলাম" vs "পেলাম"
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Left Box: ৳ দিলাম (বাকি) - Red Tint
                        val isPaboSelected = selectedTxType == "PABO"
                        Surface(
                            color = if (isPaboSelected) TaliRedLight else PureWhite,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = if (isPaboSelected) 2.dp else 1.dp,
                                    color = if (isPaboSelected) TaliRed else BorderLight,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedTxType = "PABO" }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "৳ দিলাম (বাকি)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TaliRed
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = if (isPaboSelected) amountText else "",
                                    onValueChange = { input ->
                                        selectedTxType = "PABO"
                                        amountText = input.filter { c -> c.isDigit() || c == '.' }
                                    },
                                    placeholder = { Text("০.০০", fontSize = 13.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TaliRed
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_tx_dilam_amount")
                                )
                            }
                        }

                        // Right Box: ৳ পেলাম (জমা) - Green Tint
                        val isDiboSelected = selectedTxType == "DIBO"
                        Surface(
                            color = if (isDiboSelected) TaliGreenLight else PureWhite,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = if (isDiboSelected) 2.dp else 1.dp,
                                    color = if (isDiboSelected) TaliGreen else BorderLight,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedTxType = "DIBO" }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "৳ পেলাম (জমা)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TaliGreen
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = if (isDiboSelected) amountText else "",
                                    onValueChange = { input ->
                                        selectedTxType = "DIBO"
                                        amountText = input.filter { c -> c.isDigit() || c == '.' }
                                    },
                                    placeholder = { Text("০.০০", fontSize = 13.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TaliGreen
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_tx_pelam_amount")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Preset Amounts for Dilam/Pelam Box
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "দ্রুত টাকার পরিমাণ বসান:",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val presetAmounts = listOf(
                                "100" to "+১০০",
                                "500" to "+৫০০",
                                "1000" to "+১,০০০",
                                "2000" to "+২,০০০",
                                "5000" to "+৫,০০০"
                            )
                            presetAmounts.forEach { (presetVal, label) ->
                                val activeColor = if (selectedTxType == "PABO") TaliRed else TaliGreen
                                val activeBg = if (selectedTxType == "PABO") TaliRedLight else TaliGreenLight

                                Surface(
                                    color = activeBg,
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, activeColor.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            val currentVal = amountText.toDoubleOrNull() ?: 0.0
                                            val addVal = presetVal.toDouble()
                                            val newVal = currentVal + addVal
                                            amountText = if (newVal % 1.0 == 0.0) newVal.toInt().toString() else "%.2f".format(newVal)
                                        }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = activeColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 3: Description / Note Field with Edit Icon (matching screenshot)
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("বিবরণ") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Note Icon",
                                tint = TextSecondary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_tx_note_m")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 4: Action Chips (Date Picker, Tali-message Switch, Photo Attachment)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. Date Chip (e.g. 📅 "১৭ জুলাই")
                        Surface(
                            color = SurfaceVariantLight,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.clickable { datePickerDialog.show() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = "Date",
                                    tint = TaliNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = KhataUtils.formatBengaliDayMonth(selectedDateMillis),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }

                        // 2. Tali-Message Switch Chip ("টালি-মেসেজ")
                        Surface(
                            color = SurfaceVariantLight,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "টালি-মেসেজ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = taliMessageEnabled,
                                    onCheckedChange = { taliMessageEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = PureWhite,
                                        checkedTrackColor = TaliGreen
                                    ),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        // 3. Photo Chip ("📷 ছবি")
                        Surface(
                            color = if (attachedPhotoUri != null) TaliGreenLight else SurfaceVariantLight,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.clickable {
                                photoPickerLauncher.launch("image/*")
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Photo",
                                    tint = if (attachedPhotoUri != null) TaliGreen else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (attachedPhotoUri != null) "ছবি যুক্ত" else "ছবি",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (attachedPhotoUri != null) TaliGreen else TextPrimary
                                )
                            }
                        }
                    }

                    // Attached Photo Preview Box
                    if (attachedPhotoUri != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceVariantLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                AsyncImage(
                                    model = attachedPhotoUri,
                                    contentDescription = "Attached Photo",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "সংযুক্ত মেমো/ভাউচারের ছবি",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TaliNavy
                                    )
                                    Text(
                                        text = "হিসাবের সাথে সংরক্ষিত হবে",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                IconButton(onClick = { attachedPhotoUri = null }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Photo",
                                        tint = TaliRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Row 5: Large Confirm Button ("নিশ্চিত") - Pink/Red light tint as in screenshot
                    val amountVal = amountText.toDoubleOrNull() ?: 0.0
                    Button(
                        onClick = {
                            if (amountVal > 0) {
                                onAddTransaction(selectedTxType, amountVal, noteText, paymentMethod, attachedPhotoUri?.toString())

                                val currentPhone = customer.phone
                                if (taliMessageEnabled && currentPhone.isNotBlank()) {
                                    val typeLabel = if (selectedTxType == "PABO") "দিলাম (বাকি)" else "পেলাম (জমা)"
                                    val updatedBal = if (selectedTxType == "PABO") customer.totalBalance + amountVal else customer.totalBalance - amountVal
                                    val smsBody = "শ্রদ্ধেয় ${customer.name},\n${businessName}-এ আপনার নতুন $typeLabel হিসাব ৳${KhataUtils.formatNumberOnly(amountVal)}। বিবরণ: ${noteText.ifEmpty { "নগদ/বাকি হিসাব" }}। বর্তমান বকেয়া: ${KhataUtils.formatCurrency(updatedBal)}।\nধন্যবাদ (আমার খাতা)।"

                                    try {
                                        val sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$currentPhone")).apply {
                                            putExtra("sms_body", smsBody)
                                        }
                                        context.startActivity(sendIntent)
                                        Toast.makeText(context, "সংরক্ষিত হয়েছে! মেসেজ ড্রাফট প্রস্তুত করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "লেনদেন সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "লেনদেন সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                }

                                amountText = ""
                                noteText = ""
                                attachedPhotoUri = null
                            } else {
                                Toast.makeText(context, "সঠিক টাকার পরিমাণ দিন", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF8DFDF), // Soft pink/red as in screenshot
                            contentColor = TaliRed
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_confirm_tx")
                    ) {
                        Text(
                            text = "নিশ্চিত",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TaliRed
                        )
                    }
                }
            }

            // Transaction History Header
            Text(
                text = "হিসাব বিবরণী (${transactions.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TaliNavy
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Transaction History Items List
            if (transactions.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Empty Transactions",
                            tint = TextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো পূর্বে রেকর্ডকৃত লেনদেন নেই",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(transactions, key = { it.id }) { tx ->
                        val openEdit = {
                            selectedTxForAction = tx
                            editTxAmount = if (tx.amount % 1.0 == 0.0) tx.amount.toInt().toString() else tx.amount.toString()
                            editTxNote = tx.note
                            editTxType = tx.type
                            editTxPaymentMethod = tx.paymentMethod
                            editTxDateMillis = tx.timestamp
                            showEditTxDialog = true
                        }
                        TransactionItemCard(
                            transaction = tx,
                            onClick = openEdit,
                            onEdit = openEdit,
                            onDelete = {
                                selectedTxForAction = tx
                                showDeleteTxConfirmDialog = true
                            },
                            onViewPhoto = { photoUri ->
                                viewFullPhotoUri = photoUri
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // --- DIALOGS ---
    if (showEditDialog) {
        var editName by remember { mutableStateOf(customer.name) }
        var editPhone by remember { mutableStateOf(customer.phone) }
        var editAddress by remember { mutableStateOf(customer.address) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("কাস্টমার তথ্য এডিট করুন", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("গ্রাহকের নাম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("মোবাইল নম্বর") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("ঠিকানা") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            onUpdateCustomer(customer.copy(name = editName.trim(), phone = editPhone.trim(), address = editAddress.trim()))
                            showEditDialog = false
                            Toast.makeText(context, "তথ্য আপডেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaliNavy)
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // 2. Report Statement Full Screen View (রিপোর্ট)
    if (showReportDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showReportDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ReportStatementScreen(
                customer = customer,
                allCustomers = if (allCustomers.isNotEmpty()) allCustomers else listOf(customer),
                transactions = transactions,
                onSelectCustomer = { id ->
                    onSelectCustomer(id)
                },
                onUpdateTransaction = onUpdateTransaction,
                onDeleteTransaction = onDeleteTransaction,
                onBackClick = { showReportDialog = false },
                businessName = businessName,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // 3. Edit Transaction Dialog (লেনদেন এডিট)
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
                        showDeleteTxConfirmDialog = true
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
                        value = editTxAmount,
                        onValueChange = { editTxAmount = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("টাকার পরিমাণ (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Description / Note
                    OutlinedTextField(
                        value = editTxNote,
                        onValueChange = { editTxNote = it },
                        label = { Text("বিবরণ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Payment Method Chips
                    Text("পেমেন্ট মাধ্যম:", fontSize = 12.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("ক্যাশ", "বিকাশ", "নগদ", "ব্যাংক").forEach { method ->
                            val isSelected = editTxPaymentMethod == method
                            Surface(
                                color = if (isSelected) TaliNavy else SurfaceVariantLight,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.clickable { editTxPaymentMethod = method }
                            ) {
                                Text(
                                    text = method,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PureWhite else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Date Chip
                    Surface(
                        color = SurfaceVariantLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { txDatePickerDialog.show() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = TaliNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "তারিখ: ${KhataUtils.formatBengaliFullDate(editTxDateMillis)}",
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
                        val amountVal = editTxAmount.toDoubleOrNull() ?: 0.0
                        if (amountVal > 0) {
                            val updatedTx = currentTx.copy(
                                amount = amountVal,
                                note = editTxNote.trim(),
                                type = editTxType,
                                paymentMethod = editTxPaymentMethod,
                                timestamp = editTxDateMillis
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

    // 4. Delete Transaction Confirmation Dialog
    if (showDeleteTxConfirmDialog && selectedTxForAction != null) {
        val currentTx = selectedTxForAction!!

        AlertDialog(
            onDismissRequest = { showDeleteTxConfirmDialog = false },
            title = { Text("লেনদেন ডিলিট করুন") },
            text = { Text("আপনি কি নিশ্চিতভাবে এই ${KhataUtils.formatCurrency(currentTx.amount)}-এর লেনদেনটি মুছে ফেলতে চান?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTransaction(currentTx)
                        showDeleteTxConfirmDialog = false
                        Toast.makeText(context, "লেনদেন মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("হ্যাঁ, ডিলিট করুন", color = TaliRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteTxConfirmDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // 5. Delete Customer Confirmation Dialog (ডিলিট)
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("কাস্টমার ডিলিট করুন") },
            text = { Text("আপনি কি নিশ্চিতভাবে ${customer.name}-এর সকল হিসাব ও লেনদেন মুছে ফেলতে চান?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCustomer(customer)
                        showDeleteConfirmDialog = false
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

    // 6. Full-screen Photo Viewer Dialog
    if (viewFullPhotoUri != null) {
        AlertDialog(
            onDismissRequest = { viewFullPhotoUri = null },
            title = {
                Text("মেমো/ভাউচারের ছবি", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TaliNavy)
            },
            text = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = viewFullPhotoUri,
                        contentDescription = "Bill Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewFullPhotoUri = null },
                    colors = ButtonDefaults.buttonColors(containerColor = TaliNavy)
                ) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }
}

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewPhoto: ((String) -> Unit)? = null
) {
    val isPabo = transaction.type == "PABO" // Gave / Customer owes

    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Type Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isPabo) TaliRedLight else TaliGreenLight)
            ) {
                Text(
                    text = if (isPabo) "দিলাম" else "পেলাম",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isPabo) TaliRed else TaliGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (transaction.note.isNotBlank()) transaction.note else (if (isPabo) "বাকি বিক্রি / টাকা দেওয়া" else "জমা / টাকা নেওয়া"),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = SurfaceVariantLight,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = transaction.paymentMethod,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Text(
                    text = KhataUtils.formatDate(transaction.timestamp),
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                )

                // If transaction has bill photo attached
                if (!transaction.billImageUri.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = TaliGreenLight,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable {
                            onViewPhoto?.invoke(transaction.billImageUri)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "View Photo",
                                tint = TaliGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "মেমো/ছবি দেখুন",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaliGreen
                            )
                        }
                    }
                }
            }

            // Amount & Action Buttons
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isPabo) "— " else "+ ") + KhataUtils.formatCurrency(transaction.amount),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isPabo) TaliRed else TaliGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Transaction",
                            tint = TaliNavy,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Transaction",
                            tint = TaliRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
