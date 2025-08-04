package com.sharesplit.app.data.repository.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sharesplit.app.data.model.Group
import com.sharesplit.app.data.model.User
import com.sharesplit.app.data.repository.GroupRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseGroupRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : GroupRepository {

    override fun getUserGroups(): Flow<List<Group>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("groups")
            .whereArrayContains("members", currentUser.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val groups = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Group::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(groups)
            }

        awaitClose { listener.remove() }
    }

    override fun getGroupById(groupId: String): Flow<Group?> = callbackFlow {
        val listener = firestore.collection("groups")
            .document(groupId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val group = snapshot?.toObject(Group::class.java)?.copy(id = snapshot.id)
                trySend(group)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createGroup(group: Group): Result<Group> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            val groupWithCreator = group.copy(
                createdBy = currentUser.uid,
                members = group.members + currentUser.uid
            )

            val docRef = firestore.collection("groups").add(groupWithCreator).await()
            val createdGroup = groupWithCreator.copy(id = docRef.id)
            
            Result.success(createdGroup)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGroup(group: Group): Result<Group> {
        return try {
            firestore.collection("groups").document(group.id).set(group).await()
            Result.success(group)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteGroup(groupId: String): Result<Unit> {
        return try {
            firestore.collection("groups").document(groupId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addMemberToGroup(groupId: String, userEmail: String): Result<Unit> {
        return try {
            // First, find the user by email
            val userQuery = firestore.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .await()

            val userDoc = userQuery.documents.firstOrNull()
            if (userDoc == null) {
                return Result.failure(Exception("User not found"))
            }

            val userId = userDoc.id

            // Add user to group members
            firestore.collection("groups").document(groupId)
                .update("members", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeMemberFromGroup(groupId: String, userId: String): Result<Unit> {
        return try {
            firestore.collection("groups").document(groupId)
                .update("members", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGroupMembers(groupId: String): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("groups")
            .document(groupId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val group = snapshot?.toObject(Group::class.java)
                if (group != null) {
                    // Fetch user details for all member IDs
                    val memberIds = group.members
                    if (memberIds.isNotEmpty()) {
                        firestore.collection("users")
                            .whereIn(com.google.firebase.firestore.FieldPath.documentId(), memberIds)
                            .get()
                            .addOnSuccessListener { userSnapshot ->
                                val users = userSnapshot.documents.mapNotNull { doc ->
                                    doc.toObject(User::class.java)?.copy(id = doc.id)
                                }
                                trySend(users)
                            }
                            .addOnFailureListener { exception ->
                                close(exception)
                            }
                    } else {
                        trySend(emptyList())
                    }
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun leaveGroup(groupId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            removeMemberFromGroup(groupId, currentUser.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 