package com.focusguard.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.focusguard.app.data.database.entity.AppRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppRuleDao {

    @Query("SELECT * FROM app_rules ORDER BY appName ASC")
    fun getAllRules(): Flow<List<AppRuleEntity>>

    @Query("SELECT * FROM app_rules WHERE packageName = :packageName LIMIT 1")
    suspend fun getRuleForPackage(packageName: String): AppRuleEntity?

    @Query("SELECT * FROM app_rules WHERE isBlocked = 1 OR dailyLimitMinutes > 0")
    fun getActiveRules(): Flow<List<AppRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRule(rule: AppRuleEntity)

    @Update
    suspend fun updateRule(rule: AppRuleEntity)

    @Query("DELETE FROM app_rules WHERE packageName = :packageName")
    suspend fun deleteRule(packageName: String)
}
