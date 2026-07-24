package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object LanguageUtils {

    const val LANG_BN = "BN"
    const val LANG_EN = "EN"

    fun isBangla(lang: String): Boolean = lang == LANG_BN

    // Quick text lookup helper
    fun getString(lang: String, bn: String, en: String): String {
        return if (isBangla(lang)) bn else en
    }

    // Common Labels
    fun getTabCustomers(lang: String): String = if (isBangla(lang)) "গ্রাহক খাতা" else "Customers"
    fun getTabCashbook(lang: String): String = if (isBangla(lang)) "ক্যাশ বই" else "Cashbook"
    fun getTabReports(lang: String): String = if (isBangla(lang)) "রিপোর্ট ও এআই" else "Reports & AI"
    fun getTabProfile(lang: String): String = if (isBangla(lang)) "প্রোফাইল" else "Settings"

    fun getTotalPaboLabel(lang: String): String = if (isBangla(lang)) "মোট পাবো (পাওনা)" else "Total Receivable"
    fun getTotalDiboLabel(lang: String): String = if (isBangla(lang)) "মোট দেবো (দেনা)" else "Total Payable"

    fun getOnlineLabel(lang: String): String = if (isBangla(lang)) "অনলাইন" else "Online"
    fun getOfflineLabel(lang: String): String = if (isBangla(lang)) "অফলাইন (সেভড)" else "Offline (Saved)"

    fun getSearchPlaceholder(lang: String): String = if (isBangla(lang)) "নাম বা ফোন নম্বর দিয়ে খুঁজুন..." else "Search by name or phone..."
    fun getAddCustomerBtn(lang: String): String = if (isBangla(lang)) "নতুন গ্রাহক যোগ করুন" else "Add New Customer"

    fun getCashInLabel(lang: String): String = if (isBangla(lang)) "ক্যাশ ইন (জমা)" else "Cash In (Income)"
    fun getCashOutLabel(lang: String): String = if (isBangla(lang)) "ক্যাশ আউট (খরচ)" else "Cash Out (Expense)"

    fun getGaveCreditLabel(lang: String): String = if (isBangla(lang)) "দিলাম (বাকি)" else "Gave Credit"
    fun getGotPaymentLabel(lang: String): String = if (isBangla(lang)) "পেলাম (জমা)" else "Got Payment"

    fun getSaveButtonLabel(lang: String): String = if (isBangla(lang)) "সংরক্ষণ করুন" else "Save Transaction"
}
