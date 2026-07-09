#include <jni.h>
#include <string>
#include <android/log.h>
#include "llama.h"

#define TAG "llama_jni"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static llama_model* g_model = nullptr;
static llama_context* g_ctx = nullptr;
static const llama_vocab* g_vocab = nullptr;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_cs336_tutor_data_remote_NativeBridge_initModel(
    JNIEnv *env, jclass clazz, jstring modelPath, jint contextSize) {
    
    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    LOGI("Loading model: %s", path);
    
    // Free previous
    if (g_ctx) { llama_free(g_ctx); g_ctx = nullptr; }
    if (g_model) { llama_free_model(g_model); g_model = nullptr; }
    
    ggml_backend_load_all();
    
    llama_model_params mparams = llama_model_default_params();
    mparams.n_gpu_layers = 0;
    mparams.use_mmap = true;
    
    g_model = llama_model_load_from_file(path, mparams);
    if (!g_model) {
        LOGE("Failed to load model");
        env->ReleaseStringUTFChars(modelPath, path);
        return JNI_FALSE;
    }
    
    g_vocab = llama_model_get_vocab(g_model);
    
    llama_context_params cparams = llama_context_default_params();
    cparams.n_ctx = contextSize;
    cparams.n_batch = 512;
    
    g_ctx = llama_init_from_model(g_model, cparams);
    if (!g_ctx) {
        LOGE("Failed to create context");
        llama_free_model(g_model);
        g_model = nullptr;
        env->ReleaseStringUTFChars(modelPath, path);
        return JNI_FALSE;
    }
    
    LOGI("Model loaded: n_ctx=%d", contextSize);
    env->ReleaseStringUTFChars(modelPath, path);
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_com_cs336_tutor_data_remote_NativeBridge_infer(
    JNIEnv *env, jclass clazz, jstring prompt) {
    
    if (!g_model || !g_ctx || !g_vocab) {
        return env->NewStringUTF("Model not loaded");
    }
    
    const char *input = env->GetStringUTFChars(prompt, nullptr);
    
    // Tokenize
    const int n_tokens = -llama_tokenize(g_vocab, input, strlen(input), nullptr, 0, true, true);
    auto tokens = new llama_token[n_tokens];
    llama_tokenize(g_vocab, input, strlen(input), tokens, n_tokens, true, true);
    
    // Decode
    llama_batch batch = llama_batch_get_one(tokens, n_tokens);
    if (llama_decode(g_ctx, batch) != 0) {
        delete[] tokens;
        env->ReleaseStringUTFChars(prompt, input);
        return env->NewStringUTF("Inference error");
    }
    
    // Sample
    auto sparams = llama_sampler_chain_default_params();
    sparams.no_perf = false;
    auto smpl = llama_sampler_chain_init(sparams);
    llama_sampler_chain_add(smpl, llama_sampler_init_greedy());
    
    std::string result;
    const int max_new = 256;
    for (int i = 0; i < max_new; i++) {
        llama_token token = llama_sampler_sample(smpl, g_ctx, -1);
        
        if (llama_vocab_is_eog(g_vocab, token)) break;
        
        char buf[128];
        int n = llama_token_to_piece(g_vocab, token, buf, sizeof(buf), 0, true);
        if (n < 0) break;
        result.append(buf, n);
        
        // Decode next token
        batch = llama_batch_get_one(&token, 1);
        if (llama_decode(g_ctx, batch) != 0) break;
    }
    
    llama_sampler_free(smpl);
    delete[] tokens;
    env->ReleaseStringUTFChars(prompt, input);
    
    return env->NewStringUTF(result.c_str());
}

JNIEXPORT void JNICALL
Java_com_cs336_tutor_data_remote_NativeBridge_freeModel(
    JNIEnv *env, jclass clazz) {
    if (g_ctx) { llama_free(g_ctx); g_ctx = nullptr; }
    if (g_model) { llama_free_model(g_model); g_model = nullptr; }
    g_vocab = nullptr;
    llama_backend_free();
}

} // extern "C"
