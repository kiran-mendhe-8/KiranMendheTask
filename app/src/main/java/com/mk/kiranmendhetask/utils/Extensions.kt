package com.mk.kiranmendhetask.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun Double.formatCurrency(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())
    return formatter.format(this)
}

fun Int.formatQuantity(): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(this)
}

fun Double.formatPercentage(): String {
    val formatter = DecimalFormat("#.##")
    return "${formatter.format(this)}%"
}