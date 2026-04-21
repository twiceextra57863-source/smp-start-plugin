cmake_minimum_required(VERSION 3.10)
project(vulkanboost_native)

set(CMAKE_CXX_STANDARD 17)

# Find JNI
find_package(JNI REQUIRED)

# Include JNI headers
include_directories(${JNI_INCLUDE_DIRS})

# Create the shared library
add_library(vulkanboost_native SHARED vulkanboost_native.cpp)

# Link JNI libraries
target_link_libraries(vulkanboost_native ${JNI_LIBRARIES})

# Set output directory for the compiled library
set_target_properties(vulkanboost_native PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib
    RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin
)
