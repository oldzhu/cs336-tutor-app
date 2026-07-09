cmake_minimum_required(VERSION 3.22.1)
project("llama_jni")

set(LLAMA_BUILD /tmp/llama.cpp/build-android)
set(LLAMA_SRC /tmp/llama.cpp)

include_directories(
    ${LLAMA_SRC}/include
    ${LLAMA_SRC}/ggml/include
    ${LLAMA_SRC}/src
    ${LLAMA_BUILD}/common
    ${LLAMA_BUILD}/src
    ${LLAMA_BUILD}/ggml/src
)

add_library(llama SHARED
    llama_jni.cpp
)

target_link_libraries(llama
    ${LLAMA_BUILD}/src/libllama.a
    ${LLAMA_BUILD}/common/libllama-common.a
    ${LLAMA_BUILD}/common/libllama-common-base.a
    ${LLAMA_BUILD}/ggml/src/libggml.a
    ${LLAMA_BUILD}/ggml/src/libggml-base.a
    ${LLAMA_BUILD}/ggml/src/libggml-cpu.a
    android
    log
)

target_compile_definitions(llama PRIVATE GGML_USE_CPU=1)
