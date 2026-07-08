cmake_minimum_required(VERSION 3.22.1)
project("llama_jni")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(LLAMA_DIR /tmp/llama.cpp)

add_library(llama SHARED
    llama_jni.cpp
    ${LLAMA_DIR}/src/llama.cpp
    ${LLAMA_DIR}/ggml/src/ggml.c
    ${LLAMA_DIR}/ggml/src/ggml-alloc.c
    ${LLAMA_DIR}/ggml/src/ggml-backend.cpp
    ${LLAMA_DIR}/ggml/src/ggml-quants.c
    ${LLAMA_DIR}/ggml/src/ggml.cpp
    ${LLAMA_DIR}/ggml/src/gguf.cpp
)

target_include_directories(llama PRIVATE
    ${LLAMA_DIR}/include
    ${LLAMA_DIR}/ggml/include
    ${LLAMA_DIR}/ggml/src
    ${LLAMA_DIR}/ggml/src/ggml-cpu
    ${LLAMA_DIR}/src
)

target_compile_definitions(llama PRIVATE
    GGML_USE_CPU=1
    GGML_VERSION="undefined"
    GGML_COMMIT="unknown"
)

target_compile_features(llama PRIVATE cxx_std_17)
target_link_libraries(llama android log)
