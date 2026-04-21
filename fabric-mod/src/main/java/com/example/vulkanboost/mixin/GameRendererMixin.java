package com.example.vulkanboost.mixin;

import com.example.vulkanboost.VulkanBoost;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        // Trigger native FPS boost logic at the start of every frame
        VulkanBoost.boostFPS();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderReturn(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        // Perform memory cleanup or post-processing optimizations
        VulkanBoost.optimizeMemory();
    }
}
