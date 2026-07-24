package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BorderLight
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TaliGreen
import com.example.ui.theme.TaliNavy
import com.example.ui.theme.TextSecondary
import com.example.util.LanguageUtils

@Composable
fun AppLockScreen(
    shopName: String,
    userEmail: String,
    appLanguage: String,
    onUnlock: (pinInput: String) -> Boolean,
    onRecoverPin: (emailInput: String) -> Boolean
) {
    val context = LocalContext.current
    var pinInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var recoveryEmailInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TaliNavy),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Logo
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(2.dp, TaliGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_amer_khata_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = LanguageUtils.getString(appLanguage, "আমার খাতা", "Amer Khata"),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TaliNavy
                    )
                )

                Text(
                    text = shopName,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Security Lock Icon
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = LanguageUtils.getString(appLanguage, "খাতা খুলতে পিন/পাসওয়ার্ড দিন", "Enter PIN/Password to Unlock"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TaliNavy
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input Field
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 12) pinInput = it },
                    label = { Text(LanguageUtils.getString(appLanguage, "৪ ডিজিট পিন/পাসওয়ার্ড", "4-digit PIN/Password")) },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = TaliNavy) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Unlock Button
                Button(
                    onClick = {
                        if (pinInput.isBlank()) {
                            Toast.makeText(context, LanguageUtils.getString(appLanguage, "পিন কোড লিখুন", "Please enter PIN"), Toast.LENGTH_SHORT).show()
                        } else {
                            val success = onUnlock(pinInput)
                            if (!success) {
                                pinInput = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaliGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LanguageUtils.getString(appLanguage, "আনলক করুন", "Unlock App"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Forgot Password Link
                TextButton(onClick = { showForgotDialog = true }) {
                    Text(
                        text = LanguageUtils.getString(appLanguage, "পাসওয়ার্ড ভুলে গেছেন?", "Forgot Password?"),
                        color = TaliNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    // Forgot Password Recovery Dialog
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = TaliNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LanguageUtils.getString(appLanguage, "পাসওয়ার্ড রিকভারি", "Password Recovery"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = LanguageUtils.getString(
                            appLanguage,
                            "আপনার রেজিস্টার্ড জিমেইল ($userEmail) অ্যাড্রেসটি নিচে দিয়ে ভেরিফাই করুন:",
                            "Verify your registered Gmail ($userEmail) address below:"
                        ),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = recoveryEmailInput,
                        onValueChange = { recoveryEmailInput = it },
                        label = { Text("Gmail Address") },
                        placeholder = { Text(userEmail) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val recovered = onRecoverPin(recoveryEmailInput)
                        if (recovered) {
                            showForgotDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaliGreen)
                ) {
                    Text(LanguageUtils.getString(appLanguage, "যাচাই ও রিসেট", "Verify & Reset"))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showForgotDialog = false }) {
                    Text(LanguageUtils.getString(appLanguage, "বাতিল", "Cancel"))
                }
            }
        )
    }
}
