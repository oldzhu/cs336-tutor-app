# CMake build for llama.cpp JNI bridge

cmake_minimum_required(VERSION 3.18.1)
project("llama_jni")

# llama.cpp sources
set(LLAMA_DIR ${CMAKE_SOURCE_DIR}/../../../llama.cpp)

# Core llama sources (minimal set for inference)
set(LLAMA_SOURCES
    ${LLAMA_DIR}/llama.cpp
    ${LLAMA_DIR}/llama.h
    ${LLAMA_DIR}/ggml.c
    ${LLAMA_DIR}/ggml.h
    ${LLAMA_DIR}/ggml-alloc.c
    ${LLAMA_DIR}/ggml-backend.c
)

# JNI bridge
add_library(llama SHARED
    llama_jni.cpp
    ${LLAMA_SOURCES}
)

target_include_directories(llama PRIVATE ${LLAMA_DIR})
target_compile_options(llama PRIVATE -O3 -DNDEBUG -pthread)

target_link_libraries(llama
    android
    log
)

# Android-specific flags
target_compile_definitions(llama PRIVATE GGML_USE_CPU=1)
