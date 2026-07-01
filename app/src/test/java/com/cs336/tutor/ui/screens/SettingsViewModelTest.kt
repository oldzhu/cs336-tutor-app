package com.cs336.tutor.ui.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        viewModel = SettingsViewModel(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        prefs.edit().clear().commit()
    }

    @Test
    fun `initial state is English`() {
        assertFalse(viewModel.uiState.value.isChinese)
    }

    @Test
    fun `onLanguageChanged updates state to Chinese`() {
        viewModel.onLanguageChanged(true)
        assertTrue(viewModel.uiState.value.isChinese)
    }

    @Test
    fun `saveLanguage persists Chinese to SharedPreferences`() {
        viewModel.onLanguageChanged(true)
        viewModel.saveLanguage()
        assertEquals("zh", prefs.getString("language", "en"))
    }

    @Test
    fun `saveLanguage persists English to SharedPreferences`() {
        viewModel.onLanguageChanged(false)
        viewModel.saveLanguage()
        assertEquals("en", prefs.getString("language", "en"))
    }

    @Test
    fun `loads saved Chinese preference on init`() {
        prefs.edit().putString("language", "zh").commit()
        val vm = SettingsViewModel(context)
        assertTrue(vm.uiState.value.isChinese)
    }

    @Test
    fun `loads saved English preference on init`() {
        prefs.edit().putString("language", "en").commit()
        val vm = SettingsViewModel(context)
        assertFalse(vm.uiState.value.isChinese)
    }

    @Test
    fun `default model is deepseek-v4-flash`() {
        assertEquals("deepseek-v4-flash", viewModel.uiState.value.modelName)
    }

    @Test
    fun `default endpoint is deepseek API`() {
        assertEquals("https://api.deepseek.com/v1", viewModel.uiState.value.apiEndpoint)
    }
}
