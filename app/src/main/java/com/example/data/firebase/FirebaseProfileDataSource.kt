package com.example.data.firebase

import com.example.core.model.AchievementItem
import com.example.core.model.CoinTransaction
import com.example.core.model.DailyRewardItem
import com.example.core.model.LeaderboardEntry
import com.example.core.model.League
import com.example.core.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseProfileDataSource {

    private val firestore: FirebaseFirestore? get() = FirebaseManager.getFirestore()

    suspend fun getLeaderboard(period: String = "season"): List<LeaderboardEntry> {
        val fs = firestore
        if (fs != null) {
            try {
                val snapshot = fs.collection("users")
                    .orderBy("rating", Query.Direction.DESCENDING)
                    .limit(50)
                    .get().await()

                return snapshot.documents.mapIndexed { index, doc ->
                    val user = doc.toObject(UserProfile::class.java)
                    LeaderboardEntry(
                        rank = index + 1,
                        userId = user?.userId ?: doc.id,
                        username = user?.username ?: "بازیکن",
                        rating = user?.rating ?: 1000,
                        league = user?.league ?: League.BRONZE,
                        wins = user?.stats?.wins ?: 0,
                        avatarId = user?.avatarId ?: "avatar_1"
                    )
                }
            } catch (e: Exception) {
                // fall through to empty/dev list
            }
        }
        return emptyList()
    }

    suspend fun claimDailyReward(userId: String, day: Int): Result<DailyRewardItem> {
        val fs = firestore ?: return Result.failure(Exception("سرویس در دسترس نیست."))
        return try {
            val userRef = fs.collection("users").document(userId)
            val doc = userRef.get().await()
            val user = doc.toObject(UserProfile::class.java) ?: throw Exception("کاربر یافت نشد")

            val coinsToAdd = (day * 50L) + 50L
            val xpToAdd = day * 30L

            val newCoins = user.coins + coinsToAdd
            val newXp = user.xp + xpToAdd
            val newLevel = (newXp / 250L).toInt() + 1

            // Record transaction ledger
            val transaction = CoinTransaction(
                id = UUID.randomUUID().toString(),
                userId = userId,
                amount = coinsToAdd,
                balanceBefore = user.coins,
                balanceAfter = newCoins,
                type = "DAILY_REWARD",
                reason = "جایزه ورود روز $day",
                createdAt = System.currentTimeMillis()
            )

            val batch = fs.batch()
            batch.update(userRef, mapOf(
                "coins" to newCoins,
                "xp" to newXp,
                "level" to newLevel
            ))
            batch.set(fs.collection("coinTransactions").document(transaction.id), transaction)
            batch.commit().await()

            Result.success(
                DailyRewardItem(
                    day = day,
                    rewardTitle = "سکه و تجربه",
                    coins = coinsToAdd,
                    xp = xpToAdd,
                    isClaimed = true
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun purchaseCosmetic(
        userId: String,
        itemId: String,
        cost: Long,
        isTheme: Boolean
    ): Result<Unit> {
        val fs = firestore ?: return Result.failure(Exception("سرویس در دسترس نیست."))
        return try {
            val userRef = fs.collection("users").document(userId)
            val user = userRef.get().await().toObject(UserProfile::class.java)
                ?: throw Exception("کاربر پیدا نشد.")

            if (user.coins < cost) {
                return Result.failure(Exception("سکه کافی برای خرید این آیتم ندارید."))
            }

            val newBalance = user.coins - cost
            val fieldToUpdate = if (isTheme) "activeThemeId" else "activeCardBackId"

            val transaction = CoinTransaction(
                id = UUID.randomUUID().toString(),
                userId = userId,
                amount = -cost,
                balanceBefore = user.coins,
                balanceAfter = newBalance,
                type = "PURCHASE",
                reason = "خرید آیتم $itemId",
                createdAt = System.currentTimeMillis()
            )

            val batch = fs.batch()
            batch.update(userRef, mapOf(
                "coins" to newBalance,
                fieldToUpdate to itemId
            ))
            batch.set(fs.collection("coinTransactions").document(transaction.id), transaction)
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
