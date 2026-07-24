package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object KhataUtils {
    private val engToBenDigits = mapOf(
        '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
        '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
    )

    fun toBengaliDigits(numberString: String): String {
        return numberString.map { engToBenDigits[it] ?: it }.joinToString("")
    }

    fun formatCurrency(amount: Double, currencySymbol: String = "৳"): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 0
        val formatted = formatter.format(amount)
        return "$currencySymbol ${toBengaliDigits(formatted)}"
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, yyyy - hh:mm a", Locale.getDefault())
        return toBengaliDigits(sdf.format(Date(timestamp)))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return toBengaliDigits(sdf.format(Date(timestamp)))
    }

    fun formatBengaliDayMonth(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val day = toBengaliDigits(cal.get(Calendar.DAY_OF_MONTH).toString())
        val monthNames = arrayOf(
            "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
        )
        val month = monthNames[cal.get(Calendar.MONTH)]
        return "$day $month"
    }

    fun formatBengaliFullDate(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val day = toBengaliDigits(cal.get(Calendar.DAY_OF_MONTH).toString())
        val year = toBengaliDigits(cal.get(Calendar.YEAR).toString())
        val monthNames = arrayOf(
            "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
        )
        val month = monthNames[cal.get(Calendar.MONTH)]
        return "$day $month, $year"
    }

    fun formatBengaliTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.US)
        val timeStr = sdf.format(Date(timestamp))
        return toBengaliDigits(timeStr)
    }

    fun formatNumberOnly(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 0
        return toBengaliDigits(formatter.format(amount))
    }

    fun getTimeAgoBengali(timestamp: Long): String {
        if (timestamp <= 0) return "(কোনো লেনদেন নেই)"
        val now = System.currentTimeMillis()
        val diffMs = now - timestamp
        if (diffMs < 0) return "(আজকে)"

        val days = (diffMs / (1000 * 60 * 60 * 24)).toInt()
        return when {
            days == 0 -> "(আজকে)"
            days == 1 -> "(১ দিন আগে)"
            else -> "(${toBengaliDigits(days.toString())} দিন আগে)"
        }
    }

    fun generateSmsReminder(customerName: String, balance: Double, businessName: String, phone: String = ""): String {
        val formattedAmount = formatCurrency(balance)
        return "প্রিয় $customerName,\n$businessName-এ আপনার বাকি বকেয়া হিসাব $formattedAmount। অনুগ্রহ করে পরিশোধ করবেন।\nযোগাযোগ: $phone\nধন্যবাদ (আমার খাতা)।"
    }
}

