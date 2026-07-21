package com.focusguard.app

import android.app.Application
import com.focusguard.app.data.database.FocusDatabase
import com.focusguard.app.data.datastore.PreferencesManager
import com.focusguard.app.data.repository.AppRuleRepository
import com.focusguard.app.data.repository.SettingsRepository
import com.focusguard.app.data.repository.UsageRepository
import com.focusguard.app.domain.usecase.GetInstalledAppsUseCase
import com.focusguard.app.domain.usecase.GetTodayUsageUseCase
import com.focusguard.app.service.UsageTrackingManager

class FocusGuardApplication : Application() {

    lateinit var database: FocusDatabase
        private set
    lateinit var preferencesManager: PreferencesManager
        private set

    lateinit var appRuleRepository: AppRuleRepository
        private set
    lateinit var usageRepository: UsageRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var usageTrackingManager: UsageTrackingManager
        private set
    lateinit var getInstalledAppsUseCase: GetInstalledAppsUseCase
        private set
    lateinit var getTodayUsageUseCase: GetTodayUsageUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = FocusDatabase.getInstance(this)
        preferencesManager = PreferencesManager(this)

        appRuleRepository = AppRuleRepository(database.appRuleDao())
        usageRepository = UsageRepository(database.usageRecordDao(), database.blockEventDao())
        settingsRepository = SettingsRepository(preferencesManager)

        usageTrackingManager = UsageTrackingManager(this)
        getInstalledAppsUseCase = GetInstalledAppsUseCase(this)
        getTodayUsageUseCase = GetTodayUsageUseCase(usageTrackingManager, appRuleRepository)
    }

    companion object {
        lateinit var instance: FocusGuardApplication
            private set
    }
}
