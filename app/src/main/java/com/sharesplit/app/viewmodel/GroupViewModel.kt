package com.sharesplit.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharesplit.app.data.model.Group
import com.sharesplit.app.data.model.User
import com.sharesplit.app.data.repository.GroupRepository
import com.sharesplit.app.data.repository.GoogleDriveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupState(
    val groups: List<Group> = emptyList(),
    val currentGroup: Group? = null,
    val groupMembers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val driveRepository: GoogleDriveRepository
) : ViewModel() {

    private val _groupState = MutableStateFlow(GroupState())
    val groupState: StateFlow<GroupState> = _groupState.asStateFlow()

    init {
        loadUserGroups()
    }

    fun loadUserGroups() {
        viewModelScope.launch {
            _groupState.value = _groupState.value.copy(isLoading = true, error = null)
            
            groupRepository.getUserGroups().collect { groups ->
                _groupState.value = _groupState.value.copy(
                    groups = groups,
                    isLoading = false
                )
            }
        }
    }

    fun createGroup(name: String, description: String, memberEmails: List<String>) {
        viewModelScope.launch {
            _groupState.value = _groupState.value.copy(isLoading = true, error = null)
            
            try {
                // Create Drive folder first
                val folderId = driveRepository.createGroupFolder(name)
                    .getOrThrow()
                
                val group = Group(
                    name = name,
                    description = description,
                    driveFolderId = folderId
                )
                
                val createdGroup = groupRepository.createGroup(group)
                    .getOrThrow()
                
                // Add members to the group and share Drive folder
                memberEmails.forEach { email ->
                    groupRepository.addMemberToGroup(createdGroup.id, email)
                    driveRepository.shareFolderWithUser(folderId, email)
                }
                
                loadUserGroups()
            } catch (e: Exception) {
                _groupState.value = _groupState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun selectGroup(groupId: String) {
        viewModelScope.launch {
            _groupState.value = _groupState.value.copy(isLoading = true, error = null)
            
            groupRepository.getGroupById(groupId).collect { group ->
                _groupState.value = _groupState.value.copy(
                    currentGroup = group,
                    isLoading = false
                )
                
                if (group != null) {
                    loadGroupMembers(groupId)
                }
            }
        }
    }

    private fun loadGroupMembers(groupId: String) {
        viewModelScope.launch {
            groupRepository.getGroupMembers(groupId).collect { members ->
                _groupState.value = _groupState.value.copy(
                    groupMembers = members
                )
            }
        }
    }

    fun addMemberToGroup(groupId: String, email: String) {
        viewModelScope.launch {
            _groupState.value = _groupState.value.copy(isLoading = true, error = null)
            
            try {
                groupRepository.addMemberToGroup(groupId, email)
                
                val currentGroup = _groupState.value.currentGroup
                if (currentGroup != null) {
                    driveRepository.shareFolderWithUser(currentGroup.driveFolderId, email)
                }
                
                selectGroup(groupId)
            } catch (e: Exception) {
                _groupState.value = _groupState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun removeMemberFromGroup(groupId: String, userId: String) {
        viewModelScope.launch {
            _groupState.value = _groupState.value.copy(isLoading = true, error = null)
            
            try {
                groupRepository.removeMemberFromGroup(groupId, userId)
                
                val currentGroup = _groupState.value.currentGroup
                val memberToRemove = _groupState.value.groupMembers.find { it.id == userId }
                
                if (currentGroup != null && memberToRemove != null) {
                    driveRepository.removeFolderAccess(currentGroup.driveFolderId, memberToRemove.email)
                }
                
                selectGroup(groupId)
            } catch (e: Exception) {
                _groupState.value = _groupState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            _groupState.value = _groupState.value.copy(isLoading = true, error = null)
            
            try {
                groupRepository.deleteGroup(groupId)
                loadUserGroups()
                _groupState.value = _groupState.value.copy(currentGroup = null)
            } catch (e: Exception) {
                _groupState.value = _groupState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearError() {
        _groupState.value = _groupState.value.copy(error = null)
    }
} 