cmake_minimum_required(VERSION 3.10)
project(vulkanboost_native)

set(CMAKE_CXX_STANDARD 17)

# Find JNI
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# Source files
set(SOURCES 
    vulkanboost_native.cpp
    modules/vulkan_renderer.cpp
    modules/memory_optimizer.cpp
    modules/thread_manager.cpp
    modules/math_utils.cpp
    modules/asset_preloader.cpp
)

# Create the shared library
add_library(vulkanboost_native SHARED ${SOURCES})

# Link JNI libraries
target_link_libraries(vulkanboost_native ${JNI_LIBRARIES})

# Set output properties
set_target_properties(vulkanboost_native PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib
    RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin
)
