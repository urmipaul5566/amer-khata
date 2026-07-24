package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TaliNavy
import com.example.ui.theme.TextSecondary

import com.example.util.LanguageUtils

data class NavTabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    appLanguage: String = "BN",
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavTabItem(LanguageUtils.getTabCustomers(appLanguage), Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "tab_ledger"),
        NavTabItem(LanguageUtils.getTabCashbook(appLanguage), Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "tab_cashbook"),
        NavTabItem(LanguageUtils.getTabReports(appLanguage), Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_reports"),
        NavTabItem(LanguageUtils.getTabProfile(appLanguage), Icons.Filled.Person, Icons.Outlined.Person, "tab_profile")
    )

    NavigationBar(
        containerColor = PureWhite,
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TaliNavy,
                    selectedTextColor = TaliNavy,
                    indicatorColor = TaliNavy.copy(alpha = 0.12f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}
