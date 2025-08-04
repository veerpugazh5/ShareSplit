package com.sharesplit.app.data.repository

import com.sharesplit.app.data.model.Group
import com.sharesplit.app.data.model.User
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getUserGroups(): Flow<List<Group>>
    fun getGroupById(groupId: String): Flow<Group?>
    suspend fun createGroup(group: Group): Result<Group>
    suspend fun updateGroup(group: Group): Result<Group>
    suspend fun deleteGroup(groupId: String): Result<Unit>
    suspend fun addMemberToGroup(groupId: String, userEmail: String): Result<Unit>
    suspend fun removeMemberFromGroup(groupId: String, userId: String): Result<Unit>
    suspend fun getGroupMembers(groupId: String): Flow<List<User>>
    suspend fun leaveGroup(groupId: String): Result<Unit>
} 