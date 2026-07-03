# Local LLM on Mobile: Design Discussion

**Status**: Pending · Not Started  
**Owner**: TBD

## Problem
How to run an LLM locally on a mobile device for the CS336 Tutor app, enabling offline AI-powered tutoring without requiring a network connection.

## Options

### Option A: llama.cpp + GGUF (Recommended)
- Convert models to GGUF format (4-bit / 5-bit quantization)
- Use llama.cpp Android bindings or a Kotlin JNI wrapper
- Model size: 1-3 GB for a 1B-3B parameter model
- Pros: Battle-tested, fast CPU inference, good quantized quality
- Cons: Large APK size, needs model download

### Option B: ONNX Runtime Mobile
- Export PyTorch models to ONNX format
- Run with ORT mobile package
- Pros: Flexible model format, good for small models
- Cons: Less optimized than llama.cpp for transformer models

### Option C: MediaPipe LLM
- Google's on-device LLM solution
- Pros: Official Google support, easy integration
- Cons: Limited model selection, Android-only

## Key Design Decisions

### Model Selection
- Target: 1B-3B parameter model (Qwen2.5-1.5B, Llama-3.2-1B, DeepSeek-R1-Distill-1.5B)
- Quantization: Q4_K_M or Q5_K_M
- Size: < 2GB on device

### Integration Approach
- Download model on first use (or bundle in assets)
- Separate local provider implements LLMProvider interface
- Fallback: local → remote if network available

### UX Considerations
- Show model download progress
- Allow switching between local/remote
- Indicate when using local vs remote

## Open Questions
1. How to handle model updates?
2. Battery/thermal impact of local inference?
3. Minimum device requirements?
4. How to bundle model with APK vs download on demand?

## Next Steps
- Prototype with llama.cpp Android
- Benchmark inference speed on target device
- Evaluate model quality vs size tradeoffs
