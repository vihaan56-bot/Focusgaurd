package com.focusguard.app.data.repository

import com.focusguard.app.data.database.dao.BlockEventDao
import com.focusguard.app.data.database.dao.UsageRecordDao
import com.focusguard.app.utils.TimeUtils
import kotlinx.coroutines.flow.Flow

class UsageRepository(
    private val usageRecordDao: UsageRecordDao,
    private val blockEventDao: BlockEventDao
) {

    fun getTodayBlockCount(): Flow<Int> {
        val todayEpoch = TimeUtils.getTodayEpochDay()
        return blockEventDao.getBlockCountForDay(todayEpoch)
    }
}
