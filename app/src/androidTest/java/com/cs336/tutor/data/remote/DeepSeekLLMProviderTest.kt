package com.cs336.tutor.data.remote

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeepSeekLLMProviderTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun `judge falls back without key`() = runBlocking {
        val provider = DeepSeekLLMProvider(context)
        val result = provider.judge("bpe", "code", "expected")
        assertTrue(result.passed)
        assertTrue(result.feedback.contains("Configure API key"))
    }

    @Test
    fun `q_and_a falls back without key`() = runBlocking {
        val provider = DeepSeekLLMProvider(context)
        val result = provider.answer("question", "context").first()
        assertTrue(result.text.contains("Configure API key"))
    }

    @Test
    fun `provider name is DeepSeek`() {
        val provider = DeepSeekLLMProvider(context)
        assertEquals("DeepSeek", provider.name)
    }
}
