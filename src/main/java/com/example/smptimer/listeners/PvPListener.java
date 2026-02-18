package com.example.smptimer.listeners;

import com.example.smptimer.SMPTimerPlugin;
import com.example.smptimer.utils.MessageUtils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPListener implements Listener {

    private final SMPTimerPlugin plugin;

    public PvPListener(SMPTimerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // Check if PvP is disabled and it's player vs player
        if (!plugin.isPvpEnabled() && 
            event.getEntity() instanceof Player && 
            event.getDamager() instanceof Player) {
            
            event.setCancelled(true);
            
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            
            // Spawn particles
            try {
                Particle particle = Particle.valueOf(plugin.getPluginConfig()
                    .getString("particles.pvp-block"));
                
                victim.getWorld().spawnParticle(
                    particle,
                    victim.getLocation().add(0, 1, 0),
                    30, 0.5, 0.5, 0.5, 0.1
                );
            } catch (IllegalArgumentException e) {
                // Particle not found, use default
                victim.getWorld().spawnParticle(
                    Particle.ASH,
                    victim.getLocation().add(0, 1, 0),
                    30, 0.5, 0.5, 0.5, 0.1
                );
            }
            
            // Play sound
            try {
                Sound sound = Sound.valueOf(plugin.getPluginConfig()
                    .getString("sounds.pvp-block"));
                damager.playSound(damager.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                damager.playSound(damager.getLocation(), Sound.BLOCK_LAVA_POP, 1.0f, 1.0f);
            }
            
            // Send message
            damager.sendMessage(MessageUtils.colorize(
                plugin.getPluginConfig().getString("messages.prefix") + 
                plugin.getPluginConfig().getString("messages.pvp-blocked")));
        }
    }
}
