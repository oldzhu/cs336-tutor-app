package com.cs336.tutor.ui.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        viewModel = SettingsViewModel(context)
    }

    @After
    fun tearDown() {
        prefs.edit().clear().commit()
    }

    @Test
    fun initialLanguageIsEnglish() {
        assertFalse(viewModel.uiState.value.isChinese)
    }

    @Test
    fun switchToChinese() {
        viewModel.onLanguageChanged(true)
        assertTrue(viewModel.uiState.value.isChinese)
    }

    @Test
    fun savePersistsChinese() {
        viewModel.onLanguageChanged(true)
        viewModel.saveLanguage()
        assertEquals("zh", prefs.getString("language", "en"))
    }

    @Test
    fun savePersistsEnglish() {
        viewModel.onLanguageChanged(false)
        viewModel.saveLanguage()
        assertEquals("en", prefs.getString("language", "en"))
    }

    @Test
    fun loadsChineseOnInit() {
        prefs.edit().putString("language", "zh").commit()
        val vm = SettingsViewModel(context)
        assertTrue(vm.uiState.value.isChinese)
    }

    @Test
    fun loadsEnglishOnInit() {
        prefs.edit().putString("language", "en").commit()
        val vm = SettingsViewModel(context)
        assertFalse(vm.uiState.value.isChinese)
    }

    @Test
    fun defaultModelIsFlash() {
        assertEquals("deepseek-v4-flash", viewModel.uiState.value.modelName)
    }
}
