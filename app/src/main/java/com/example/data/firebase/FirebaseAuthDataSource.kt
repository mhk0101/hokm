package com.example.data.firebase

import com.example.core.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource {

    private val auth: FirebaseAuth? get() = FirebaseManager.getAuth()
    private val firestore: FirebaseFirestore? get() = FirebaseManager.getFirestore()

    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile = _currentUserProfile.asStateFlow()

    fun observeAuthState(): Flow<String?> = callbackFlow {
        val authInstance = auth
        if (authInstance == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            trySend(uid)
        }
        authInstance.addAuthStateListener(listener)
        awaitClose {
            authInstance.removeAuthStateListener(listener)
        }
    }

    suspend fun checkUsernameAvailable(username: String): Boolean {
        val fs = firestore ?: return true
        val trimmed = username.trim().lowercase()
        val doc = fs.collection("usernames").document(trimmed).get().await()
        return !doc.exists()
    }

    suspend fun registerUser(
        email: String,
        pass: String,
        username: String
    ): Result<UserProfile> {
        val authInstance = auth ?: return Result.failure(Exception("سرویس احراز هویت در دسترس نیست."))
        val fs = firestore ?: return Result.failure(Exception("پایگاه داده در دسترس نیست."))

        val trimmedUsername = username.trim()
        if (trimmedUsername.length !in 3..20) {
            return Result.failure(Exception("نام کاربری باید بین ۳ تا ۲۰ کاراکتر باشد."))
        }

        val isAvailable = checkUsernameAvailable(trimmedUsername)
        if (!isAvailable) {
            return Result.failure(Exception("این نام کاربری قبلاً انتخاب شده است."))
        }

        return try {
            val authResult = authInstance.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("خطا در ایجاد شناسه کاربری")

            val initialProfile = UserProfile(
                userId = uid,
                username = trimmedUsername,
                email = email,
                avatarId = "avatar_1",
                rating = 1000,
                xp = 0L,
                level = 1,
                coins = 500L,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )

            // Claim username atomically
            val batch = fs.batch()
            val userRef = fs.collection("users").document(uid)
            val usernameRef = fs.collection("usernames").document(trimmedUsername.lowercase())

            batch.set(userRef, initialProfile)
            batch.set(usernameRef, mapOf("userId" to uid, "createdAt" to System.currentTimeMillis()))
            batch.commit().await()

            _currentUserProfile.value = initialProfile
            Result.success(initialProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, pass: String): Result<UserProfile> {
        val authInstance = auth ?: return Result.failure(Exception("سرویس احراز هویت در دسترس نیست."))
        val fs = firestore ?: return Result.failure(Exception("پایگاه داده در دسترس نیست."))

        return try {
            val authResult = authInstance.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("کاربر یافت نشد")

            val profileDoc = fs.collection("users").document(uid).get().await()
            val profile = profileDoc.toObject(UserProfile::class.java)
                ?: UserProfile(userId = uid, email = email, username = email.substringBefore("@"))

            _currentUserProfile.value = profile
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInAsGuest(username: String): Result<UserProfile> {
        val authInstance = auth
        val fs = firestore

        val trimmed = username.trim()
        val generatedUid = "guest_${System.currentTimeMillis()}"

        val profile = UserProfile(
            userId = authInstance?.currentUser?.uid ?: generatedUid,
            username = trimmed.ifEmpty { "بازیکن_${(1000..9999).random()}" },
            rating = 1000,
            coins = 500L
        )

        return try {
            if (authInstance != null) {
                val result = authInstance.signInAnonymously().await()
                val uid = result.user?.uid ?: generatedUid
                val finalProfile = profile.copy(userId = uid)
                fs?.collection("users")?.document(uid)?.set(finalProfile)?.await()
                _currentUserProfile.value = finalProfile
                Result.success(finalProfile)
            } else {
                _currentUserProfile.value = profile
                Result.success(profile)
            }
        } catch (e: Exception) {
            _currentUserProfile.value = profile
            Result.success(profile)
        }
    }

    suspend fun loadUserProfile(uid: String): UserProfile? {
        val fs = firestore ?: return _currentUserProfile.value
        return try {
            val doc = fs.collection("users").document(uid).get().await()
            val profile = doc.toObject(UserProfile::class.java)
            if (profile != null) {
                _currentUserProfile.value = profile
            }
            profile
        } catch (e: Exception) {
            null
        }
    }

    fun signOut() {
        auth?.signOut()
        _currentUserProfile.value = null
    }
}
