package com.sharesplit.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Settlement(
    val id: String = "",
    val groupId: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val paidBy: String = "", // User ID
    val paidTo: String = "", // User ID
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable 