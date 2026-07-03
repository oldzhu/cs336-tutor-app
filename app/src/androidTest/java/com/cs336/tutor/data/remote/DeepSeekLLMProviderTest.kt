package com.cs336.tutor.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.cs336.tutor.domain.model.JudgeResult
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
    private lateinit var prefs: SharedPreferences

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun `judge falls back without key`() = runBlocking {
        prefs.edit().putString("api_key", "").commit()
        val provider = DeepSeekLLMProvider(context)
        // Override prefs path for test
        val result = provider.judge("bpe", "code", "expected")
        assertTrue(result.passed)
        assertTrue(result.feedback.contains("Configure API key"))
    }

    @Test
    fun `hasKey returns false when key empty`() {
        prefs.edit().putString("api_key", "").commit()
        val provider = DeepSeekLLMProvider(context)
        assertFalse(provider.hasKeyForTest())
    }

    @Test
    fun `hasKey returns false when key whitespace only`() {
        prefs.edit().putString("api_key", "   \n  ").commit()
        val provider = DeepSeekLLMProvider(context)
        assertFalse(provider.hasKeyForTest())
    }

    @Test
    fun `hasKey returns true when key present`() {
        prefs.edit().putString("api_key", "sk-test123").commit()
        val provider = DeepSeekLLMProvider(context)
        assertTrue(provider.hasKeyForTest())
    }

    @Test
    fun `key is trimmed of whitespace`() {
        prefs.edit().putString("api_key", "  sk-test-key  \n").commit()
        val provider = DeepSeekLLMProvider(context)
        assertEquals("sk-test-key", provider.getKeyForTest())
    }

    @Test
    fun `Q&A falls back without key`() = runBlocking {
        prefs.edit().putString("api_key", "").commit()
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
