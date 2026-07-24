package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CashbookEntry
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
fun CashbookScreen(
    cashbookEntries: List<CashbookEntry>,
    totalCashIn: Double,
    totalCashOut: Double,
    onAddEntry: (type: String, amount: Double, category: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var entryTypeToAdd by remember { mutableStateOf("CASH_IN") }

    val netCash = totalCashIn - totalCashOut

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Net Cash Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = TaliNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "আজকের ক্যাশ ইন হ্যান্ড (ক্যাশ ব্যালেন্স)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PureWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = KhataUtils.formatCurrency(netCash),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("মোট ক্যাশ ইন (জমা)", fontSize = 11.sp, color = PureWhite.copy(alpha = 0.7f))
                            Text(
                                KhataUtils.formatCurrency(totalCashIn),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6EE7B7)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("মোট ক্যাশ আউট (খরচ)", fontSize = 11.sp, color = PureWhite.copy(alpha = 0.7f))
                            Text(
                                KhataUtils.formatCurrency(totalCashOut),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }
            }

            // Entry List Header
            Text(
                text = "আজকের ক্যাশ এন্ট্রি (${cashbookEntries.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TaliNavy
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (cashbookEntries.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Empty Cashbook",
                            tint = TextSecondary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ক্যাশবুকে কোনো এন্ট্রি নেই",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "দৈনিক ক্যাশ বেচাকেনা বা দোকান খরচ এখানে সেভ করুন",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(cashbookEntries, key = { it.id }) { entry ->
                        val isCashIn = entry.type == "CASH_IN"
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (isCashIn) TaliGreenLight else TaliRedLight)
                                ) {
                                    Text(
                                        text = if (isCashIn) "ইন" else "আউট",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isCashIn) TaliGreen else TaliRed
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (entry.note.isNotBlank()) entry.note else (if (isCashIn) "নগদ বেচাকেনা" else "দোকান খরচ/পেমেন্ট"),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${entry.category} • ${KhataUtils.formatDate(entry.timestamp)}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Text(
                                    text = (if (isCashIn) "+ " else "— ") + KhataUtils.formatCurrency(entry.amount),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isCashIn) TaliGreen else TaliRed
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Add FAB
        FloatingActionButton(
            onClick = { showAddSheet = true },
            containerColor = TaliNavy,
            contentColor = PureWhite,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_cashbook_fab")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Cash Entry")
                Spacer(modifier = Modifier.width(6.dp))
                Text("ক্যাশ এন্ট্রি", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showAddSheet) {
        AddCashbookBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { type, amount, category, note ->
                onAddEntry(type, amount, category, note)
                showAddSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCashbookBottomSheet(
    onDismiss: () -> Unit,
    onSave: (type: String, amount: Double, category: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var type by remember { mutableStateOf("CASH_IN") } // CASH_IN or CASH_OUT
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("সাধারণ বেচাকেনা") }

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
                text = "ক্যাশ এন্ট্রি সেভ করুন",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TaliNavy
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Type Toggle
            Row(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = if (type == "CASH_IN") TaliGreen else SurfaceVariantLight,
                    shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clickable {
                            type = "CASH_IN"
                            category = "সাধারণ বেচাকেনা"
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "+ ক্যাশ ইন (জমা)",
                            color = if (type == "CASH_IN") PureWhite else TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = if (type == "CASH_OUT") TaliRed else SurfaceVariantLight,
                    shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clickable {
                            type = "CASH_OUT"
                            category = "দোকান খরচ"
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "— ক্যাশ আউট (খরচ)",
                            color = if (type == "CASH_OUT") PureWhite else TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("টাকার পরিমাণ (৳) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_cash_amount")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("বিবরণ (যেমন: বিদ্যুৎ বিল / মালামাল ক্রয়)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onSave(type, amount, category, note)
                    }
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = if (type == "CASH_IN") TaliGreen else TaliRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("এন্ট্রি সেভ করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
