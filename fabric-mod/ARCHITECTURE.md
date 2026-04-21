# VulkanBoost Mod Architecture

## Overview
VulkanBoost is a high-performance Fabric mod for Minecraft 1.21.4 designed to provide 200+ FPS and extreme smoothness. It achieves this by offloading heavy rendering and memory management tasks to a native C++ layer using JNI (Java Native Interface).

## Key Components

### 1. Java Layer (Fabric/Mixin)
- **Entrypoint**: Initializes the mod and loads the native library.
- **Mixins**: Hooks into `GameRenderer` and `LevelRenderer` to redirect calls to the native Vulkan-like engine.
- **Event Hooks**: Monitors game state to trigger proactive optimizations.

### 2. Native Layer (C++)
- **VulkanEngine**: A high-performance core that batches draw calls and manages render commands with minimal overhead.
- **MemoryManager**: Optimized C++ memory allocation to reduce Java GC pressure and prevent lag spikes.
- **ThreadManager**: Parallelizes chunk rendering and entity processing using native threads.

### 3. Optimization Features
- **Zero-Lag Camera**: Decouples camera movement from main game ticks for ultra-smooth rotation.
- **Batch Rendering**: Groups similar render calls to reduce draw call overhead.
- **Explosion Buffer**: Pre-calculates particle and block updates during explosions to prevent FPS spikes.
- **Entity Culling**: Advanced frustum and occlusion culling for players and mobs in SMPs.

## Build System
- **Gradle**: Manages Java dependencies and Fabric Loom.
- **CMake**: Compiles the C++ native library for Windows, Linux, and macOS.
- **GitHub Actions**: Automated CI/CD to compile both Java and C++ components into a single JAR.

## Performance Targets
- **FPS**: 200+ on mid-range hardware.
- **Smoothness**: Consistent frame times even during heavy explosions or high player counts.
- **Compatibility**: Works with LTW, Zink, and OpenGL renderers without crashes.
