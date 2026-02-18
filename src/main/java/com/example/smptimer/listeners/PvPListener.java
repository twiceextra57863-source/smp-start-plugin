package com.example.smptimer.listeners;

import com.example.smptimer.SMPTimerPlugin;
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
        if (!plugin.isPvpEnabled() && event.getEntity() instanceof Player && 
            event.getDamager() instanceof Player) {
            
            event.setCancelled(true);
            
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            
            try {
                // Show ash particles
                victim.getWorld().spawnParticle(
                    Particle.ASH,
                    victim.getLocation().add(0, 1, 0),
                    30, 0.5, 0.5, 0.5, 0.1
                );
                
                // Play sound
                damager.playSound(damager.getLocation(), Sound.BLOCK_LAVA_POP, 0.5f, 1.0f);
                
            } catch (Exception e) {
                // Fallback if particles/sounds don't exist
            }
            
            // Send message
            damager.sendMessage("§6[SMP] §cPvP is not enabled until the SMP starts!");
            damager.sendMessage("§6[SMP] §eTime left: §a" + formatTime(plugin.getTimeLeft()));
        }
    }
    
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
