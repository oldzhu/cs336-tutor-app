# Feature: Chat History & Session Context in Q&A

**Status**: Planned · Not Started  
**Created**: 2026-07-06

## Problem
The Q&A feature in each component tutor only shows the latest question/reply. Previous exchanges are overwritten. Users lose conversation context when asking follow-up questions.

## Solution
Maintain a chat history for each component session, displaying all previous Q&A exchanges like ChatGPT.

### UI
- Chat-style layout with scrollable message list
- User messages (right-aligned) and AI responses (left-aligned)
- Thinking indicator while AI responds
- Clear/reset chat button

### Data Model
```kotlin
data class ChatMessage(
    val role: String,      // "user" or "assistant"
    val content: String,
    val timestamp: Long
)
```

### Context for LLM
Send full conversation history to LLM as context:
```json
{
  "messages": [
    {"role": "user", "content": "Explain this code..."},
    {"role": "assistant", "content": "This code..."},
    {"role": "user", "content": "What about line 5?"}
  ]
}
```

### Scope
- Per-component chat history (cleared when leaving component)
- Optional: persistent history (Room DB)

## Dependencies
- LLM provider (✅ done)
- SplitScreenTutorViewModel (needs chat state)
- UI: ChatBubble composable
