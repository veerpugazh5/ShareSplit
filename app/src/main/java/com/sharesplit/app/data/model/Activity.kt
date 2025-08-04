package com.sharesplit.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Activity(
    val id: String = "",
    val groupId: String = "",
    val type: ActivityType = ActivityType.EXPENSE_ADDED,
    val title: String = "",
    val description: String = "",
    val userId: String = "",
    val relatedId: String? = null, // Expense ID, Settlement ID, etc.
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

enum class ActivityType {
    EXPENSE_ADDED,
    EXPENSE_EDITED,
    EXPENSE_DELETED,
    SETTLEMENT_ADDED,
    MEMBER_ADDED,
    MEMBER_REMOVED,
    GROUP_CREATED,
    GROUP_UPDATED
} 