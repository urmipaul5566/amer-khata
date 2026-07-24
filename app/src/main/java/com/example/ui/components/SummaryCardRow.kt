package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TaliGreen
import com.example.ui.theme.TaliGreenLight
import com.example.ui.theme.TaliRed
import com.example.ui.theme.TaliRedLight
import com.example.util.KhataUtils

import com.example.util.LanguageUtils

@Composable
fun SummaryCardRow(
    totalPabo: Double,
    totalDibo: Double,
    currencySymbol: String = "৳",
    appLanguage: String = "BN",
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Pabo Card (I Will Receive / Customer Debts)
        Card(
            colors = CardDefaults.cardColors(containerColor = TaliGreenLight),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .border(1.dp, TaliGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(TaliGreen, shape = RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Pabo",
                            tint = PureWhite,
                            modifier = Modifier.height(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LanguageUtils.getTotalPaboLabel(appLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TaliGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = KhataUtils.formatCurrency(totalPabo, currencySymbol),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TaliGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        }

        // Dibo Card (I Will Pay / Supplier Debts)
        Card(
            colors = CardDefaults.cardColors(containerColor = TaliRedLight),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .border(1.dp, TaliRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(TaliRed, shape = RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Dibo",
                            tint = PureWhite,
                            modifier = Modifier.height(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LanguageUtils.getTotalDiboLabel(appLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TaliRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = KhataUtils.formatCurrency(totalDibo, currencySymbol),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TaliRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        }
    }
}
