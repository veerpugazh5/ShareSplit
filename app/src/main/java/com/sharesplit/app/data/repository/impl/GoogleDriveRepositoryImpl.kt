package com.sharesplit.app.data.repository.impl

import android.content.Context
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission
import com.sharesplit.app.data.repository.GoogleDriveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleDriveRepositoryImpl @Inject constructor(
    private val context: Context
) : GoogleDriveRepository {

    private fun getDriveService(): Drive? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account == null) return null

        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE)
        ).apply {
            selectedAccount = account.account
        }

        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName("ShareSplit")
            .build()
    }

    override suspend fun createGroupFolder(groupName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService() ?: return@withContext Result.failure(Exception("Drive service not available"))
            
            val folderMetadata = File().apply {
                name = "ShareSplit - $groupName"
                mimeType = "application/vnd.google-apps.folder"
            }

            val folder = driveService.files().create(folderMetadata)
                .setFields("id")
                .execute()

            Result.success(folder.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadFileToFolder(folderId: String, fileName: String, fileUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService() ?: return@withContext Result.failure(Exception("Drive service not available"))
            
            // Read file content
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val bytes = inputStream?.readBytes() ?: return@withContext Result.failure(Exception("Could not read file"))
            inputStream.close()

            val fileMetadata = File().apply {
                name = fileName
                parents = listOf(folderId)
            }

            val mediaContent = ByteArrayContent("image/*", bytes)
            val file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()

            Result.success(file.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFile(fileId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService() ?: return@withContext Result.failure(Exception("Drive service not available"))
            driveService.files().delete(fileId).execute()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFileUrl(fileId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService() ?: return@withContext Result.failure(Exception("Drive service not available"))
            
            val file = driveService.files().get(fileId)
                .setFields("webContentLink")
                .execute()

            val url = file.webContentLink ?: return@withContext Result.failure(Exception("File URL not available"))
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun shareFolderWithUser(folderId: String, userEmail: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService() ?: return@withContext Result.failure(Exception("Drive service not available"))
            
            val permission = Permission().apply {
                type = "user"
                role = "writer"
                emailAddress = userEmail
            }

            driveService.permissions().create(folderId, permission).execute()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFolderAccess(folderId: String, userEmail: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService() ?: return@withContext Result.failure(Exception("Drive service not available"))
            
            // Get permissions for the folder
            val permissions = driveService.permissions().list(folderId).execute()
            val permission = permissions.permissions.find { it.emailAddress == userEmail }
            
            if (permission != null) {
                driveService.permissions().delete(folderId, permission.id).execute()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUserAuthenticated(): Boolean = withContext(Dispatchers.IO) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        account != null
    }

    override suspend fun requestDrivePermissions(): Result<Unit> = withContext(Dispatchers.IO) {
        // This would typically be handled by the UI layer with GoogleSignInClient
        // For now, we'll just check if the user is authenticated
        if (isUserAuthenticated()) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("User not authenticated"))
        }
    }
} 