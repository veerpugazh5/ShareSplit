package com.sharesplit.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Expense(
    val id: String = "",
    val groupId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val date: Long = System.currentTimeMillis(),
    val paidBy: String = "", // User ID
    val splitAmong: List<String> = emptyList(), // User IDs
    val splitType: SplitType = SplitType.EQUAL,
    val customSplits: Map<String, Double> = emptyMap(), // User ID to amount
    val billImageDriveId: String? = null,
    val billImageUrl: String? = null,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class SplitType {
    EQUAL,
    PERCENTAGE,
    CUSTOM
} 