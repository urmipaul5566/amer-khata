package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.Customer
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    customers: List<Customer>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCustomerClick: (Long) -> Unit,
    onAddCustomer: (name: String, phone: String, address: String, customerType: String) -> Unit,
    businessName: String,
    modifier: Modifier = Modifier
) {
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, PABO, DIBO, SUPPLIER

    val filteredList = customers.filter { customer ->
        val matchesSearch = customer.name.contains(searchQuery, ignoreCase = true) ||
                customer.phone.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "PABO" -> customer.totalBalance > 0
            "DIBO" -> customer.totalBalance < 0
            "SUPPLIER" -> customer.customerType == "SUPPLIER"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Input Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("কাস্টমারের নাম বা মোবাইল নম্বর দিয়ে খুঁজুন...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TaliNavy,
                    unfocusedBorderColor = BorderLight,
                    focusedContainerColor = PureWhite,
                    unfocusedContainerColor = SurfaceVariantLight
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("customer_search_input")
            )

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("সকল কাস্টমার (${customers.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TaliNavy,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "PABO",
                        onClick = { selectedFilter = "PABO" },
                        label = { Text("বাকি পাবেন (${customers.count { it.totalBalance > 0 }})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TaliGreen,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "DIBO",
                        onClick = { selectedFilter = "DIBO" },
                        label = { Text("দেনা আছে (${customers.count { it.totalBalance < 0 }})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TaliRed,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "SUPPLIER",
                        onClick = { selectedFilter = "SUPPLIER" },
                        label = { Text("সাপ্লায়ার (${customers.count { it.customerType == "SUPPLIER" }})") }
                    )
                }
            }

            // Customer List
            if (filteredList.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Empty",
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "কোনো কাস্টমার পাওয়া যায়নি" else "এখনো কোনো কাস্টমার যুক্ত করেননি",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "নিচের '+' বাটনে ট্যাপ করে নতুন কাস্টমার এর খাতা খুলুন",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(filteredList, key = { it.id }) { customer ->
                        CustomerItemCard(
                            customer = customer,
                            onClick = { onCustomerClick(customer.id) },
                            onCallClick = {
                                if (customer.phone.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                                    context.startActivity(intent)
                                }
                            },
                            onSmsClick = {
                                if (customer.phone.isNotBlank()) {
                                    val smsText = KhataUtils.generateSmsReminder(customer.name, customer.totalBalance, businessName)
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${customer.phone}")).apply {
                                        putExtra("sms_body", smsText)
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        // Add FAB Button
        FloatingActionButton(
            onClick = { showAddBottomSheet = true },
            containerColor = TaliGreen,
            contentColor = PureWhite,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_customer_fab")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
                Spacer(modifier = Modifier.width(6.dp))
                Text("নতুন কাস্টমার", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Modal Bottom Sheet to Add Customer
    if (showAddBottomSheet) {
        AddCustomerBottomSheet(
            onDismiss = { showAddBottomSheet = false },
            onSave = { name, phone, address, type ->
                onAddCustomer(name, phone, address, type)
                showAddBottomSheet = false
            }
        )
    }
}

@Composable
fun CustomerItemCard(
    customer: Customer,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onSmsClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("customer_item_${customer.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Customer Initials Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            customer.totalBalance > 0 -> TaliGreenLight
                            customer.totalBalance < 0 -> TaliRedLight
                            else -> SurfaceVariantLight
                        }
                    )
            ) {
                Text(
                    text = customer.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = when {
                            customer.totalBalance > 0 -> TaliGreen
                            customer.totalBalance < 0 -> TaliRed
                            else -> TaliNavy
                        },
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = customer.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                    )
                    if (customer.customerType == "SUPPLIER") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = SurfaceVariantLight,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "সাপ্লায়ার",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(
                    text = if (customer.phone.isNotBlank()) customer.phone else "মোবাইল নম্বর নেই",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp)
                )
            }

            // Balance Indicator Tag
            Column(horizontalAlignment = Alignment.End) {
                val balance = customer.totalBalance
                when {
                    balance > 0 -> {
                        Text(
                            text = "পাবো",
                            fontSize = 11.sp,
                            color = TaliGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = KhataUtils.formatCurrency(balance),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TaliGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                    balance < 0 -> {
                        Text(
                            text = "দিবো",
                            fontSize = 11.sp,
                            color = TaliRed,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = KhataUtils.formatCurrency(kotlin.math.abs(balance)),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TaliRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                    else -> {
                        Text(
                            text = "পরিশোধিত",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = KhataUtils.formatCurrency(0.0),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }

                // Quick Phone / SMS Actions
                if (customer.phone.isNotBlank()) {
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        IconButton(
                            onClick = onCallClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = TaliGreen, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = onSmsClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Sms, contentDescription = "SMS", tint = TaliNavy, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerBottomSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, address: String, customerType: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var customerType by remember { mutableStateOf("CUSTOMER") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PureWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "নতুন কাস্টমার যোগ করুন",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TaliNavy
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("কাস্টমার / কাস্টমারের নাম *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_name")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("মোবাইল নম্বর (ঐচ্ছিক)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_phone")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("ঠিকানা (ঐচ্ছিক)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("টাইপ নির্বাচন করুন:", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = customerType == "CUSTOMER",
                    onClick = { customerType = "CUSTOMER" },
                    colors = RadioButtonDefaults.colors(selectedColor = TaliNavy)
                )
                Text("সাধারণ কাস্টমার", modifier = Modifier.clickable { customerType = "CUSTOMER" })

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = customerType == "SUPPLIER",
                    onClick = { customerType = "SUPPLIER" },
                    colors = RadioButtonDefaults.colors(selectedColor = TaliNavy)
                )
                Text("সাপ্লায়ার (পাইকারি)", modifier = Modifier.clickable { customerType = "SUPPLIER" })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, phone, address, customerType)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TaliGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_customer_button")
            ) {
                Text("কাস্টমার সেভ করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
