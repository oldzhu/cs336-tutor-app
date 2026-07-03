# Future Feature: Multimodal Teaching & Learning

**Status**: Planned · Not Started  
**Created**: 2026-07-03

## Summary
Enhance the CS336 Tutor with multimodal capabilities — voice, animation, vision, and video — to create a richer, more engaging learning experience that goes beyond text-based tutoring.

## Capabilities

### 🎤 Voice
- **Text-to-Speech (TTS)**: Read code explanations aloud
- **Speech-to-Text (STT)**: Voice-input questions and code
- **Conversational tutoring**: Voice-based Q&A like a real tutor

### ✨ Animation
- **Training dynamics visualization**: Watch loss curves, gradient flow, attention patterns in real-time
- **Algorithm animations**: BPE merge steps, RoPE rotations, attention weight matrices animated
- **Data flow diagrams**: Tokens flowing through the pipeline step by step

### 👁️ Vision
- **Handwritten math recognition**: Write formulas on screen → parse to LaTeX/executable code
- **Diagram understanding**: Upload architecture diagrams → AI explains them
- **Code screenshot OCR**: Take a photo of code → import into the editor

### 🎬 Video
- **Mini-lectures**: Short animated videos explaining each component
- **Code walkthrough recordings**: Screen recording of coding with voiceover
- **Peer learning clips**: Share and discuss video explanations

## Technical Approaches

| Feature | Android API | Library Options |
|---|---|---|
| TTS | `TextToSpeech` (built-in) | Edge TTS, Google TTS |
| STT | `SpeechRecognizer` (built-in) | Whisper.cpp, Vosk |
| Animation | Jetpack Compose `Animatable` | Lottie, Canvas API |
| Vision/OCR | CameraX + ML Kit | Tesseract, EasyOCR |
| Video | Media3/ExoPlayer | FFmpeg |

## Dependencies
- Real LLM provider (✅ done)
- Local model for offline voice (pending)
- Camera permission for vision features

## Open Questions
1. Should animations be pre-rendered or generated on-device?
2. Voice in which languages? (CN/EN minimum)
3. How much of this runs offline vs cloud?
4. Battery/performance impact on mobile?

## Phased Approach
- **Phase A**: TTS for explanations (lowest effort, high impact)
- **Phase B**: STT for Q&A input
- **Phase C**: Attention visualization animations
- **Phase D**: Vision-based code import
- **Phase E**: Full video lectures
