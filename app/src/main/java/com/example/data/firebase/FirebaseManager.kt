package com.example.data.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {

    private var isInitialized = false
    private var initializationError: String? = null

    fun checkOrInitialize(context: Context): Boolean {
        if (isInitialized) return true
        return try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isEmpty()) {
                val app = FirebaseApp.initializeApp(context)
                isInitialized = app != null
            } else {
                isInitialized = true
            }
            if (!isInitialized) {
                initializationError = "فایل تنظیمات google-services.json شناسایی نشد یا ناقص است."
            }
            isInitialized
        } catch (e: Exception) {
            initializationError = e.localizedMessage ?: "خطای ناشناخته در اتصال به فایربیس"
            false
        }
    }

    val isFirebaseConfigured: Boolean get() = isInitialized

    val statusMessage: String
        get() = if (isInitialized) {
            "فایربیس متصل و فعال است."
        } else {
            initializationError ?: "فایربیس هنوز پیکربندی نشده است. لطفاً google-services.json را اضافه کنید."
        }

    fun getAuth(): FirebaseAuth? = if (isInitialized) runCatching { FirebaseAuth.getInstance() }.getOrNull() else null
    fun getFirestore(): FirebaseFirestore? = if (isInitialized) runCatching { FirebaseFirestore.getInstance() }.getOrNull() else null
    fun getDatabase(): FirebaseDatabase? = if (isInitialized) runCatching { FirebaseDatabase.getInstance() }.getOrNull() else null
}
