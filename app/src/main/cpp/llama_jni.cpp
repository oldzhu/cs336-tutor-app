#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include "llama.h"

#define TAG "llama_jni"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static llama_model* g_model = nullptr;
static llama_context* g_ctx = nullptr;
static const llama_vocab* g_vocab = nullptr;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_cs336_tutor_data_remote_NativeBridge_initModel(
    JNIEnv *env, jclass clazz, jstring modelPath, jint contextSize) {
    
    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    
    if (g_ctx) { llama_free(g_ctx); g_ctx = nullptr; }
    if (g_model) { llama_free_model(g_model); g_model = nullptr; g_vocab = nullptr; }
    
    ggml_backend_load_all();
    
    g_model = llama_model_load_from_file(path, llama_model_default_params());
    if (!g_model) {
        LOGE("FAIL: model NULL"); env->ReleaseStringUTFChars(modelPath, path); return JNI_FALSE;
    }
    
    g_vocab = llama_model_get_vocab(g_model);
    
    llama_context_params cparams = llama_context_default_params();
    cparams.n_ctx = contextSize;
    cparams.n_batch = 2048;
    cparams.n_threads = 4;
    cparams.n_threads_batch = 4;
    
    g_ctx = llama_init_from_model(g_model, cparams);
    if (!g_ctx) {
        LOGE("FAIL: context"); llama_free_model(g_model); g_model = nullptr; g_vocab = nullptr;
        env->ReleaseStringUTFChars(modelPath, path); return JNI_FALSE;
    }
    
    LOGE("init OK (ctx=%d, batch=%d)", contextSize, 2048);
    env->ReleaseStringUTFChars(modelPath, path);
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_com_cs336_tutor_data_remote_NativeBridge_infer(
    JNIEnv *env, jclass clazz, jstring prompt) {
    
    if (!g_model || !g_ctx || !g_vocab) return env->NewStringUTF("not loaded");
    
    const char *input = env->GetStringUTFChars(prompt, nullptr);
    int len = strlen(input);
    if (len > 2048) len = 2048;
    
    const int n_tokens = -llama_tokenize(g_vocab, input, len, nullptr, 0, true, true);
    LOGE("tokens: %d", n_tokens);
    
    if (n_tokens < 1 || n_tokens > 2048) {
        env->ReleaseStringUTFChars(prompt, input);
        return env->NewStringUTF(n_tokens < 1 ? "empty" : "too long");
    }
    
    std::vector<llama_token> tokens(n_tokens);
    llama_tokenize(g_vocab, input, len, tokens.data(), n_tokens, true, true);
    
    // Decode ALL tokens in ONE batch
    llama_batch batch = llama_batch_get_one(tokens.data(), n_tokens);
    LOGE("decode ALL %d tokens...", n_tokens);
    
    int rc = llama_decode(g_ctx, batch);
    LOGE("decode rc=%d", rc);
    
    if (rc != 0) {
        env->ReleaseStringUTFChars(prompt, input);
        return env->NewStringUTF("decode err");
    }
    
    // Sample
    auto smpl = llama_sampler_chain_init(llama_sampler_chain_default_params());
    llama_sampler_chain_add(smpl, llama_sampler_init_greedy());
    
    std::string result;
    for (int i = 0; i < 128; i++) {
        llama_token token = llama_sampler_sample(smpl, g_ctx, -1);
        if (llama_vocab_is_eog(g_vocab, token)) break;
        
        char buf[256];
        int n = llama_token_to_piece(g_vocab, token, buf, sizeof(buf), 0, true);
        if (n < 0) break;
        result.append(buf, n);
        
        batch = llama_batch_get_one(&token, 1);
        if (llama_decode(g_ctx, batch) != 0) break;
    }
    
    llama_sampler_free(smpl);
    env->ReleaseStringUTFChars(prompt, input);
    
    LOGE("output: %zu", result.size());
    return env->NewStringUTF(result.empty() ? "(empty)" : result.c_str());
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
