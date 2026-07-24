package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TaliGreen
import com.example.ui.theme.TaliNavy
import com.example.ui.theme.TextSecondary

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Language
import com.example.ui.theme.TaliGreenLight
import com.example.util.LanguageUtils

@Composable
fun HeaderBar(
    businessName: String,
    ownerName: String,
    isOnline: Boolean,
    appLanguage: String = "BN",
    onLanguageToggle: () -> Unit = {},
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = PureWhite,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_amer_khata_logo),
                            contentDescription = "Amer Khata Logo",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = businessName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TaliNavy,
                                fontSize = 18.sp
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ownerName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            NetworkStatusBadge(isOnline = isOnline, appLanguage = appLanguage)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Language Switcher Badge
                    Surface(
                        color = TaliGreenLight,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clickable { onLanguageToggle() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Switch Language",
                                tint = TaliNavy,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (appLanguage == "BN") "বাংলা" else "ENG",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaliNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TaliNavy
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkStatusBadge(isOnline: Boolean, appLanguage: String = "BN") {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isOnline) Color(0xFFE6F4EA) else Color(0xFFFEF3C7),
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isOnline) TaliGreen else Color(0xFFD97706))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isOnline) LanguageUtils.getOnlineLabel(appLanguage) else LanguageUtils.getOfflineLabel(appLanguage),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isOnline) TaliGreen else Color(0xFFB45309),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            )
        }
    }
}
