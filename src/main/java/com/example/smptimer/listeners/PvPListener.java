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
            
            // Show ash particles
            victim.getWorld().spawnParticle(
                Particle.valueOf(plugin.getConfig().getString("particles.hit")),
                victim.getLocation().add(0, 1, 0),
                30, 0.5, 0.5, 0.5, 0.1
            );
            
            // Play sound
            damager.playSound(damager.getLocation(), 
                            Sound.valueOf(plugin.getConfig().getString("sounds.hit")), 1.0f, 1.0f);
            
            // Send message
            damager.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                               "§cPvP is not enabled until the SMP starts!");
        }
    }
}
