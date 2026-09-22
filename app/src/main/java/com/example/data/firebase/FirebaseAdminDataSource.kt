package com.example.data.firebase

import com.example.core.model.AdminLog
import com.example.core.model.UserReport
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseAdminDataSource {

    private val firestore: FirebaseFirestore? get() = FirebaseManager.getFirestore()

    suspend fun submitReport(report: UserReport): Result<Unit> {
        val fs = firestore ?: return Result.failure(Exception("سرویس در دسترس نیست."))
        return try {
            val reportId = UUID.randomUUID().toString()
            val finalReport = report.copy(reportId = reportId, createdAt = System.currentTimeMillis())
            fs.collection("reports").document(reportId).set(finalReport).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReports(): List<UserReport> {
        val fs = firestore ?: return emptyList()
        return try {
            val snapshot = fs.collection("reports").orderBy("createdAt").limit(30).get().await()
            snapshot.documents.mapNotNull { it.toObject(UserReport::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun setUserBanStatus(adminId: String, targetUserId: String, isBanned: Boolean): Result<Unit> {
        val fs = firestore ?: return Result.failure(Exception("سرویس در دسترس نیست."))
        return try {
            val batch = fs.batch()
            batch.update(fs.collection("users").document(targetUserId), "isBanned", isBanned)

            val log = AdminLog(
                logId = UUID.randomUUID().toString(),
                adminId = adminId,
                targetUserId = targetUserId,
                action = if (isBanned) "BAN_USER" else "UNBAN_USER",
                details = "وضعیت مسدودیت کاربر تغییر یافت."
            )
            batch.set(fs.collection("adminLogs").document(log.logId), log)
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
