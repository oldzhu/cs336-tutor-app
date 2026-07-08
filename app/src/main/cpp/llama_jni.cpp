#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "llama_jni"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// Stub implementation — will be replaced with real llama.cpp calls
// once libllama.so is built with llama.cpp sources

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_cs336_tutor_data_remote_NativeBridge_initModel(
    JNIEnv *env, jclass clazz, jstring modelPath, jint contextSize) {
    
    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    LOGI("initModel called with path: %s, context: %d", path, contextSize);
    env->ReleaseStringUTFChars(modelPath, path);
    
    // TODO: Initialize llama.cpp context
    // llama_model_params params = llama_model_default_params();
    // llama_model *model = llama_load_model_from_file(path, params);
    
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_com_cs336_tutor_data_remote_NativeBridge_infer(
    JNIEnv *env, jclass clazz, jstring prompt) {
    
    const char *input = env->GetStringUTFChars(prompt, nullptr);
    LOGI("infer called with prompt length: %zu", strlen(input));
    
    // TODO: Run inference
    // Run llama_eval() and return generated text
    std::string response = "Local inference not yet available. Native bridge loaded.";
    
    env->ReleaseStringUTFChars(prompt, input);
    return env->NewStringUTF(response.c_str());
}

JNIEXPORT void JNICALL
Java_com_cs336_tutor_data_remote_NativeBridge_freeModel(
    JNIEnv *env, jclass clazz) {
    LOGI("freeModel called");
    // TODO: Free llama.cpp resources
}

} // extern "C"
