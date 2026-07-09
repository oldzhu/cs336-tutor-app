cmake_minimum_required(VERSION 3.22.1)
project("llama_jni")

# Link against llama.cpp static libraries (submodule at repo root)
if(NOT DEFINED LLAMA_BUILD_DIR)
    set(LLAMA_BUILD_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../llama.cpp/build-android)
endif()

add_library(llama SHARED
    llama_jni.cpp
)

target_include_directories(llama PRIVATE
    ${LLAMA_BUILD_DIR}/ggml/src
    ${LLAMA_BUILD_DIR}/src
    ${CMAKE_CURRENT_SOURCE_DIR}/../../../llama.cpp/include
    ${CMAKE_CURRENT_SOURCE_DIR}/../../../llama.cpp/ggml/include
    ${CMAKE_CURRENT_SOURCE_DIR}/../../../llama.cpp/src
)

# Link all required static libraries
target_link_libraries(llama
    ${LLAMA_BUILD_DIR}/src/libllama.a
    ${LLAMA_BUILD_DIR}/common/libllama-common.a
    ${LLAMA_BUILD_DIR}/common/libllama-common-base.a
    ${LLAMA_BUILD_DIR}/ggml/src/libggml.a
    ${LLAMA_BUILD_DIR}/ggml/src/libggml-base.a
    ${LLAMA_BUILD_DIR}/ggml/src/libggml-cpu.a
    android
    log
)

target_compile_definitions(llama PRIVATE GGML_USE_CPU=1)
