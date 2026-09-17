package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.AppLanguage
import com.example.data.model.CyclePhase
import com.example.data.model.CycleSharingMode
import com.example.data.model.QuoteMood
import com.example.data.model.getStrings
import com.example.data.repository.CoupleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MYLY", appName)
    }

    @Test
    fun `multilingual localization strings exist and match keys`() {
        val enStrings = getStrings(AppLanguage.ENGLISH)
        val msStrings = getStrings(AppLanguage.BAHASA_MELAYU)
        val idStrings = getStrings(AppLanguage.BAHASA_INDONESIA)

        assertEquals("MYLY", enStrings.appTitle)
        assertEquals("Bersama", msStrings.tabTogether)
        assertEquals("Bersama", idStrings.tabTogether)
        assertEquals("Thinking of You", enStrings.thinkingOfYou)
    }

    @Test
    fun `couple repository streak checkin updates state correctly`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = AppDatabase.getDatabase(context)
        val repository = CoupleRepository(database)

        val initialStreak = repository.streakInfo.value.currentStreak
        repository.checkInCurrentPartner()
        // Check that check-in was registered
        assertTrue(repository.streakInfo.value.partnerACheckedIn || repository.streakInfo.value.partnerBCheckedIn)
    }

    @Test
    fun `cycle privacy sharing modes can be adjusted`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = AppDatabase.getDatabase(context)
        val repository = CoupleRepository(database)

        repository.updateCycleSharingMode(CycleSharingMode.PHASE_ONLY)
        assertEquals(CycleSharingMode.PHASE_ONLY, repository.cycleStatus.value.sharingMode)

        repository.updateCycleSharingMode(CycleSharingMode.PRIVATE)
        assertEquals(CycleSharingMode.PRIVATE, repository.cycleStatus.value.sharingMode)
    }
}
