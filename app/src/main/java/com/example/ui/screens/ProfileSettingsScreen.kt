package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.BusinessProfile
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceVariantLight
import com.example.ui.theme.TaliGreen
import com.example.ui.theme.TaliGreenLight
import com.example.ui.theme.TaliNavy
import com.example.ui.theme.TaliRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.KhataUtils

import androidx.compose.material.icons.filled.Language
import com.example.util.LanguageUtils

@Composable
fun ProfileSettingsScreen(
    profile: BusinessProfile?,
    isLoggedIn: Boolean,
    userEmail: String,
    userName: String,
    isFirebaseSyncing: Boolean,
    lastSyncTime: Long,
    appLanguage: String = "BN",
    isPinEnabled: Boolean = false,
    onLanguageChange: (String) -> Unit = {},
    onEnablePinLock: (pin: String) -> Unit = {},
    onDisablePinLock: () -> Unit = {},
    onSaveProfile: (name: String, owner: String, phone: String, address: String, currency: String) -> Unit,
    onGoogleLogin: (email: String, name: String) -> Unit,
    onLogout: () -> Unit,
    onFirebaseSync: () -> Unit,
    onExportBackupJson: () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var shopName by remember(profile) { mutableStateOf(profile?.businessName ?: "আমার খাতা স্টোর") }
    var ownerName by remember(profile) { mutableStateOf(profile?.ownerName ?: "প্রোপাইটর") }
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var address by remember(profile) { mutableStateOf(profile?.address ?: "") }
    var currency by remember(profile) { mutableStateOf(profile?.currency ?: "৳") }

    var showGoogleLoginDialog by remember { mutableStateOf(false) }
    var loginEmailInput by remember { mutableStateOf(userEmail) }
    var loginNameInput by remember { mutableStateOf(userName) }

    var showSetPinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_amer_khata_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(46.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = LanguageUtils.getString(appLanguage, "আমার খাতা", "Amer Khata"),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TaliNavy
                    )
                )
                Text(
                    text = LanguageUtils.getString(appLanguage, "ভার্সন ১.০ (ফায়ারবেস ক্লাউড সিকিউরড)", "Version 1.0 (Firebase Cloud Secured)"),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Language Selector Card (বাংলা & English)
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
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = TaliNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LanguageUtils.getString(appLanguage, "অ্যাপের ভাষা / App Language", "App Language / অ্যাপের ভাষা"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TaliNavy
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Bangla Toggle
                    Button(
                        onClick = { onLanguageChange("BN") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (appLanguage == "BN") TaliNavy else PureWhite,
                            contentColor = if (appLanguage == "BN") PureWhite else TaliNavy
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TaliNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("বাংলা (Bangla)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // English Toggle
                    Button(
                        onClick = { onLanguageChange("EN") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (appLanguage == "EN") TaliNavy else PureWhite,
                            contentColor = if (appLanguage == "EN") PureWhite else TaliNavy
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TaliNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("English", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Security / Password & PIN Protection Card
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (isPinEnabled) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPinEnabled) Icons.Default.Lock else Icons.Default.Security,
                                    contentDescription = "Lock Security",
                                    tint = if (isPinEnabled) TaliGreen else Color(0xFFD32F2F),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = LanguageUtils.getString(appLanguage, "লগইন পাসওয়ার্ড / পিন নিরাপত্তা", "Login Password / PIN Security"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TaliNavy
                            )
                            Text(
                                text = if (isPinEnabled) LanguageUtils.getString(appLanguage, "পাসওয়ার্ড লক সক্রিয় আছে", "Password Lock Enabled") else LanguageUtils.getString(appLanguage, "পাসওয়ার্ড লক বন্ধ আছে", "Password Lock Disabled"),
                                fontSize = 12.sp,
                                color = if (isPinEnabled) TaliGreen else TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Security Badge
                    Surface(
                        color = if (isPinEnabled) Color(0xFFE8F5E9) else Color(0xFFEEEEEE),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = if (isPinEnabled) LanguageUtils.getString(appLanguage, "সক্রিয়", "Active") else LanguageUtils.getString(appLanguage, "নিষ্ক্রিয়", "Inactive"),
                            color = if (isPinEnabled) TaliGreen else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = LanguageUtils.getString(
                        appLanguage,
                        "অ্যাপ খুলতে পাসওয়ার্ড বা পিন কোড সেট করুন যাতে আপনার অনুপস্থিতিতে অন্য কেউ আপনার খাতা দেখতে না পারে।",
                        "Set a password or PIN code so no unauthorized person can view your business ledger when you are away."
                    ),
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (isPinEnabled) {
                        Button(
                            onClick = { showSetPinDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TaliNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(LanguageUtils.getString(appLanguage, "পিন পরিবর্তন", "Change PIN"), fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onDisablePinLock,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(LanguageUtils.getString(appLanguage, "লক বন্ধ করুন", "Disable Lock"), fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { showSetPinDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TaliGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(LanguageUtils.getString(appLanguage, "পাসওয়ার্ড / পিন লক চালু করুন", "Enable Password / PIN Lock"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 1. Google / Gmail Firebase Login & Cloud Sync Card
        Card(
            colors = CardDefaults.cardColors(containerColor = TaliNavy),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = PureWhite.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Google Account",
                                    tint = PureWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "গুগল ও ফায়ারবেস অ্যাকাউন্ট",
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (isLoggedIn) "জিমেইল যুক্ত আছে" else "লগইন প্রয়োজন",
                                color = PureWhite.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Security Status Badge
                    Surface(
                        color = if (isLoggedIn) Color(0xFF2E7D32) else Color(0xFFD84315),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isLoggedIn) Icons.Default.CheckCircle else Icons.Default.Lock,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLoggedIn) "সুরক্ষিত" else "অফলাইন",
                                color = PureWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isLoggedIn) {
                    // Logged in user info
                    Surface(
                        color = PureWhite.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = userEmail,
                                    color = PureWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PureWhite.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "মালিক: $userName",
                                    color = PureWhite.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "সর্বশেষ ফায়ারবেস ব্যাকআপ: ${KhataUtils.formatDate(lastSyncTime)}",
                                color = PureWhite.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = TaliGreenLight,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "লাইফটাইম অটো ব্যাকআপ সক্রিয় (অ্যাপ আনইনস্টল করলেও ডাটা ডিলিট হবে না)",
                                    color = TaliGreenLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Cloud Sync Button
                        Button(
                            onClick = onFirebaseSync,
                            colors = ButtonDefaults.buttonColors(containerColor = TaliGreen),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isFirebaseSyncing,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isFirebaseSyncing) {
                                CircularProgressIndicator(
                                    color = PureWhite,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = "Sync", modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isFirebaseSyncing) "সিঙ্ক হচ্ছে..." else "ক্লাউড সিঙ্ক", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Change Account / Logout Button
                        OutlinedButton(
                            onClick = { showGoogleLoginDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PureWhite.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Login, contentDescription = "Switch Account", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("আইডি পরিবর্তন", fontSize = 12.sp)
                        }
                    }
                } else {
                    // Not logged in UI call to action
                    Text(
                        text = "আপনার দোকানের হিসাব নিরাপদ রাখতে আপনার গুগল (Gmail) আইডি দিয়ে লগইন করুন। এর মাধ্যমে ফোন হারালেও ফায়ারবেস ক্লাউড থেকে সকল হিসাব ফেরত পাবেন।",
                        color = PureWhite.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showGoogleLoginDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = TaliGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Login, contentDescription = "Login")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("গুগল (Gmail) আইডি দিয়ে লগইন করুন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 2. Shop Profile Edit Card
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "দোকানের প্রোফাইল তথ্য",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TaliNavy
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("দোকান / ব্যবসার নাম *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("মালিকের নাম (প্রোপাইটর)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("দোকানের ঠিকানা") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSaveProfile(shopName, ownerName, phone, address, currency)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaliGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("প্রোফাইল সেভ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 3. Database Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantLight),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = "Database", tint = TaliNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ডাটাবেজ ও অফলাইন সুরক্ষা",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TaliNavy
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• আপনার সকল হিসাব অ্যান্ড্রয়েড Room Database ও ফায়ারবেস ক্লাউডে ১০০% নিরাপদে সংরক্ষিত আছে।\n• ইন্টারনেট না থাকলেও সম্পূর্ণ অ্যাপ অফলাইনে কাজ করবে।\n• ডাটা ব্যাকআপ রাখতে নিচের ব্যাকআপ বাটনে চাপ দিন।",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = {
                            val json = onExportBackupJson()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("AmerKhataBackup", json)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "ডাটা ব্যাকআপ ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ডাটা কপি", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val json = onExportBackupJson()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, json)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "ডাটা ব্যাকআপ শেয়ার করুন"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ডাটা ব্যাকআপ", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Google / Gmail Login Dialog Modal
    if (showGoogleLoginDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleLoginDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TaliNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("গুগল (Gmail) আইডি দিয়ে লগইন", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TaliNavy)
                }
            },
            text = {
                Column {
                    Text(
                        text = "আপনার জিমেইল অ্যাকাউন্ট কানেক্ট করলে আপনার দোকানের সকল হিসাব ফায়ারবেস ক্লাউডে স্বয়ংক্রিয়ভাবে ব্যাকআপ থাকবে।",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = loginEmailInput,
                        onValueChange = { loginEmailInput = it },
                        label = { Text("আপনার জিমেইল (Gmail) অ্যাড্রেস *") },
                        placeholder = { Text("gopalkumarpaul55@gmail.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TaliNavy) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = loginNameInput,
                        onValueChange = { loginNameInput = it },
                        label = { Text("মালিকের নাম / গুগল ইউজারনেম") },
                        placeholder = { Text("গোপাল কুমার পাল") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TaliNavy) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (loginEmailInput.isNotBlank() && loginEmailInput.contains("@")) {
                            onGoogleLogin(loginEmailInput, loginNameInput)
                            showGoogleLoginDialog = false
                        } else {
                            Toast.makeText(context, "সঠিক জিমেইল আইডি দিন", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaliGreen)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("লগইন ও ব্যাকআপ শুরু করুন")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showGoogleLoginDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Set or Change PIN Dialog Modal
    if (showSetPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showSetPinDialog = false
                newPinInput = ""
                confirmPinInput = ""
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = TaliNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LanguageUtils.getString(appLanguage, "পাসওয়ার্ড / পিন সেট করুন", "Set Password / PIN"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TaliNavy
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = LanguageUtils.getString(
                            appLanguage,
                            "খাতায় লগইন করতে আপনার ৪ থেকে ১২ ডিজিটের নতুন পিন/পাসওয়ার্ড লিখুন:",
                            "Enter your 4 to 12 digit new PIN/password to protect your ledger:"
                        ),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 12) newPinInput = it },
                        label = { Text(LanguageUtils.getString(appLanguage, "নতুন পিন/পাসওয়ার্ড লিখুন *", "Enter New PIN/Password *")) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TaliNavy) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = confirmPinInput,
                        onValueChange = { if (it.length <= 12) confirmPinInput = it },
                        label = { Text(LanguageUtils.getString(appLanguage, "পুনরায় নতুন পিন দিন *", "Confirm New PIN *")) },
                        leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TaliNavy) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length < 4) {
                            Toast.makeText(context, LanguageUtils.getString(appLanguage, "কমপক্ষে ৪ ডিজিটের পিন দিন", "PIN must be at least 4 digits"), Toast.LENGTH_SHORT).show()
                        } else if (newPinInput != confirmPinInput) {
                            Toast.makeText(context, LanguageUtils.getString(appLanguage, "পিন দুটি মেলেনি! পুনরায় চেষ্টা করুন", "PINs do not match! Try again"), Toast.LENGTH_SHORT).show()
                        } else {
                            onEnablePinLock(newPinInput)
                            showSetPinDialog = false
                            newPinInput = ""
                            confirmPinInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaliGreen)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(LanguageUtils.getString(appLanguage, "সেভ করুন", "Save PIN"))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showSetPinDialog = false
                    newPinInput = ""
                    confirmPinInput = ""
                }) {
                    Text(LanguageUtils.getString(appLanguage, "বাতিল", "Cancel"))
                }
            }
        )
    }
}
