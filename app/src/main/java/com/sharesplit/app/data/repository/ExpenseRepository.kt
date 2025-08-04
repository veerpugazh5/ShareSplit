package com.sharesplit.app.data.repository

import android.net.Uri
import com.sharesplit.app.data.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getGroupExpenses(groupId: String): Flow<List<Expense>>
    fun getExpenseById(expenseId: String): Flow<Expense?>
    suspend fun createExpense(expense: Expense, billImageUri: Uri? = null): Result<Expense>
    suspend fun updateExpense(expense: Expense, billImageUri: Uri? = null): Result<Expense>
    suspend fun deleteExpense(expenseId: String): Result<Unit>
    suspend fun uploadBillImage(groupId: String, imageUri: Uri): Result<String> // Returns Drive file ID
    suspend fun deleteBillImage(driveFileId: String): Result<Unit>
    suspend fun getBillImageUrl(driveFileId: String): Result<String>
} 