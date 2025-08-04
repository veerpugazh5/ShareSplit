package com.sharesplit.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String? = null,
    val driveFolderId: String = "",
    val members: List<String> = emptyList(), // User IDs
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable 