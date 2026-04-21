package com.example.vulkanboost;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VulkanBoost implements ModInitializer {
    public static final String MOD_ID = "vulkanboost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    static {
        try {
            System.loadLibrary("vulkanboost_native");
            LOGGER.info("VulkanBoost native library loaded successfully!");
        } catch (UnsatisfiedLinkError e) {
            LOGGER.error("Failed to load VulkanBoost native library. Performance will be degraded.", e);
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("VulkanBoost initializing for Minecraft 1.21.4...");
        initializeNative();
    }

    private native void initializeNative();
    public static native void boostFPS();
    public static native void optimizeMemory();
}
