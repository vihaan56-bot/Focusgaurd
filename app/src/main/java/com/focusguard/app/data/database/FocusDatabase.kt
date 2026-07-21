package com.focusguard.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.focusguard.app.data.database.dao.AppRuleDao
import com.focusguard.app.data.database.dao.BlockEventDao
import com.focusguard.app.data.database.dao.FocusSessionDao
import com.focusguard.app.data.database.dao.UsageRecordDao
import com.focusguard.app.data.database.entity.AppRuleEntity
import com.focusguard.app.data.database.entity.BlockEventEntity
import com.focusguard.app.data.database.entity.FocusSessionEntity
import com.focusguard.app.data.database.entity.UsageRecordEntity

@Database(
    entities = [
        AppRuleEntity::class,
        UsageRecordEntity::class,
        BlockEventEntity::class,
        FocusSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FocusDatabase : RoomDatabase() {

    abstract fun appRuleDao(): AppRuleDao
    abstract fun usageRecordDao(): UsageRecordDao
    abstract fun blockEventDao(): BlockEventDao
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        @Volatile
        private var INSTANCE: FocusDatabase? = null

        fun getInstance(context: Context): FocusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusDatabase::class.java,
                    "focusguard_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
