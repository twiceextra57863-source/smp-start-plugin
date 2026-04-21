# VulkanBoost Mod Architecture

## Overview
VulkanBoost is a high-performance Fabric mod for Minecraft 1.21.4 designed to provide 200+ FPS and extreme smoothness. It achieves this by offloading heavy rendering and memory management tasks to a native C++ layer using JNI (Java Native Interface).

## Key Components

### 1. Java Layer (Fabric/Mixin)
- **Entrypoint**: Initializes the mod and loads the native library.
- **Mixins**: Hooks into Minecraft's rendering pipeline (LWJGL/OpenGL) to redirect calls to the native Vulkan-like engine.
- **Event Hooks**: Monitors game state (explosions, player movement, block placement) to trigger proactive optimizations.

### 2. Native Layer (C++)
- **VulkanBridge**: A thin wrapper that mimics Vulkan's efficiency on top of existing renderers (OpenGL, Zink, LTW).
- **MemoryManager**: Optimized C++ memory allocation to reduce Java GC pressure.
- **ThreadManager**: Parallelizes chunk rendering and entity processing using native threads.
- **LagReducer**: Specific logic to handle camera movement, block placement, and high player counts without frame drops.

### 3. Optimization Features
- **Zero-Lag Camera**: Decouples camera movement from main game ticks for ultra-smooth rotation.
- **Batch Rendering**: Groups similar render calls to reduce draw call overhead.
- **Explosion Buffer**: Pre-calculates particle and block updates during explosions to prevent FPS spikes.
- **Entity Culling**: Advanced frustum and occlusion culling for players and mobs in SMPs.

## Build System
- **Gradle**: Manages Java dependencies and Fabric Loom.
- **CMake**: Compiles the C++ native library for Windows, Linux, and macOS.
- **GitHub Actions**: Automated CI/CD to compile both Java and C++ components into a single JAR.
