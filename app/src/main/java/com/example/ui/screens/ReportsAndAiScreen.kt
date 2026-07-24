package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsAndAiScreen(
    customers: List<Customer>,
    transactions: List<TransactionEntity>,
    selectedCustomer: Customer?,
    onSelectCustomer: (Long) -> Unit,
    onUpdateTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    businessName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val totalPabo = remember(customers) { customers.filter { it.totalBalance > 0 }.sumOf { it.totalBalance } }
    val totalDibo = remember(customers) { customers.filter { it.totalBalance < 0 }.sumOf { kotlin.math.abs(it.totalBalance) } }
    val paboCustomersCount = remember(customers) { customers.count { it.totalBalance > 0 } }

    var aiQuery by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf("আমার খাতা এআই: আপনার দোকানের হিসাব বিশ্লেষণ সম্পূর্ণ হয়েছে। কোনো প্রশ্ন থাকলে নিচে লিখুন।") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TaliNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "বিজনেস রিপোর্ট ও এআই",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TaliNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        containerColor = PureWhite,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureWhite)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Cards Header
            Card(
                colors = CardDefaults.cardColors(containerColor = TaliNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "$businessName - ব্যবসার মূল হিসাব",
                        color = PureWhite.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("মোট পাবো (বকেয়া)", color = Color(0xFFFFCDD2), fontSize = 12.sp)
                            Text(
                                text = KhataUtils.formatCurrency(totalPabo),
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("মোট দিবো (দেনা)", color = Color(0xFFC8E6C9), fontSize = 12.sp)
                            Text(
                                text = KhataUtils.formatCurrency(totalDibo),
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = PureWhite.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("মোট কাস্টমার সংখ্যা:", color = PureWhite, fontSize = 12.sp)
                            Text("${customers.size} জন", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // AI Smart Assistant Section
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceVariantLight),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "AI Tip",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "আমার খাতা এআই অ্যাসিস্ট্যান্ট",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TaliNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // AI Insight text
                    Surface(
                        color = PureWhite,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = aiResponse,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // AI Query Field
                    OutlinedTextField(
                        value = aiQuery,
                        onValueChange = { aiQuery = it },
                        placeholder = { Text("এআই-কে প্রশ্ন করুন (যেমন: বকেয়া কত?)", fontSize = 12.sp) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (aiQuery.isBlank()) return@IconButton
                                    val q = aiQuery.trim()
                                    aiResponse = when {
                                        q.contains("বকেয়া") || q.contains("পাবো") ->
                                            "আপনার মোট বকেয়া (পাবো) পরিমাণ ${KhataUtils.formatCurrency(totalPabo)}। মোট $paboCustomersCount জন কাস্টমারের কাছে পাওনা রয়েছে।"
                                        q.contains("দেনা") || q.contains("দিবো") ->
                                            "আপনার মোট দেনা (দিবো) পরিমাণ ${KhataUtils.formatCurrency(totalDibo)}।"
                                        q.contains("টিপস") || q.contains("পরামর্শ") ->
                                            "টিপস: যেসব কাস্টমারের বকেয়া ৩০ দিনের বেশি, তাদের সাপ্তাহিক SMS রিমাইন্ডার পাঠান।"
                                        else ->
                                            "আমার খাতা এআই: \"$q\" প্রশ্নের ভিত্তিতে আপনার ব্যবসায় মোট পাবো ${KhataUtils.formatCurrency(totalPabo)} এবং মোট দিবো ${KhataUtils.formatCurrency(totalDibo)}।"
                                    }
                                    aiQuery = ""
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = TaliNavy)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Quick Actions & Reports Export Section
            Text(
                text = "রিপোর্ট অ্যাকশন",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TaliNavy
            )

            // Button 1: Export / Share Full Business Statement
            Button(
                onClick = {
                    val fullReportText = buildString {
                        append("📊 $businessName - সামারি রিপোর্ট\n")
                        append("------------------------------------\n")
                        append("মোট পাবো (বকেয়া): ${KhataUtils.formatCurrency(totalPabo)}\n")
                        append("মোট দিবো (দেনা): ${KhataUtils.formatCurrency(totalDibo)}\n")
                        append("মোট কাস্টমার: ${customers.size} জন\n")
                        append("------------------------------------\n")
                        append("শীর্ষ বকেয়া কাস্টমার তালিকা:\n")
                        customers.filter { it.totalBalance > 0 }.take(10).forEach { c ->
                            append("• ${c.name} (${c.phone}): ${KhataUtils.formatCurrency(c.totalBalance)}\n")
                        }
                        append("\nধন্যবাদ (আমার খাতা অ্যাপ)।")
                    }

                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, fullReportText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "বিজনেসের রিপোর্ট শেয়ার করুন"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = TaliNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_export_full_report")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("সম্পূর্ণ বিজনেসের রিপোর্ট শেয়ার / ডাউনলোড করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            // Button 2: Bulk SMS Reminders to Due Customers
            Button(
                onClick = {
                    val dueCustomers = customers.filter { it.totalBalance > 0 }
                    if (dueCustomers.isEmpty()) {
                        Toast.makeText(context, "কোনো বকেয়া কাস্টমার নেই", Toast.LENGTH_SHORT).show()
                    } else {
                        val firstDue = dueCustomers.first()
                        val smsText = KhataUtils.generateSmsReminder(firstDue.name, firstDue.totalBalance, businessName, firstDue.phone)
                        val sendIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("sms:${firstDue.phone}")).apply {
                            putExtra("sms_body", smsText)
                        }
                        context.startActivity(sendIntent)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TaliRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("বকেয়া কাস্টমারদের SMS রিমাইন্ডার পাঠান", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
