package com.sharesplit.app.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface GoogleDriveRepository {
    suspend fun createGroupFolder(groupName: String): Result<String> // Returns folder ID
    suspend fun uploadFileToFolder(folderId: String, fileName: String, fileUri: Uri): Result<String> // Returns file ID
    suspend fun deleteFile(fileId: String): Result<Unit>
    suspend fun getFileUrl(fileId: String): Result<String>
    suspend fun shareFolderWithUser(folderId: String, userEmail: String): Result<Unit>
    suspend fun removeFolderAccess(folderId: String, userEmail: String): Result<Unit>
    suspend fun isUserAuthenticated(): Boolean
    suspend fun requestDrivePermissions(): Result<Unit>
} 