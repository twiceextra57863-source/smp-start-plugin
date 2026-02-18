package com.example.smptimer.listeners;

import com.example.smptimer.SMPTimerPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    private final SMPTimerPlugin plugin;

    public PlayerListener(SMPTimerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.isTimerRunning()) {
            // Send info to joining player
            int timeLeft = plugin.getTimeLeft();
            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;
            
            player.sendMessage("§6§m----------------------------------------");
            player.sendMessage("§6[SMP] §eSMP Countdown in progress!");
            player.sendMessage("§6[SMP] §eTime remaining: §a" + String.format("%02d:%02d", minutes, seconds));
            player.sendMessage("§6[SMP] §7Please wait at spawn for the start!");
            player.sendMessage("§6§m----------------------------------------");
            
            // Teleport to spawn if they're far
            Location spawn = player.getWorld().getSpawnLocation();
            if (player.getLocation().distance(spawn) > 50) {
                player.teleport(spawn);
                player.sendMessage("§6[SMP] §eYou have been teleported to spawn!");
            }
            
        } else if (plugin.isPvpEnabled()) {
            // Welcome message when SMP is active
            player.sendMessage("§6§m----------------------------------------");
            player.sendMessage("§6[SMP] §aWelcome to the active SMP!");
            player.sendMessage("§6[SMP] §ePvP is enabled - Good luck!");
            player.sendMessage("§6§m----------------------------------------");
        } else {
            // Server is idle
            player.sendMessage("§6[SMP] §7Server is idle. Use /smp start <minutes> to begin!");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Freeze players during countdown (except admins)
        if (plugin.isTimerRunning() && plugin.getTimeLeft() > 0) {
            Player player = event.getPlayer();
            
            // Check if player has permission to move
            if (!player.hasPermission("smptimer.admin")) {
                // Check if they're trying to move significantly
                Location from = event.getFrom();
                Location to = event.getTo();
                
                if (from.getBlockX() != to.getBlockX() || 
                    from.getBlockY() != to.getBlockY() || 
                    from.getBlockZ() != to.getBlockZ()) {
                    
                    // Cancel movement
                    event.setCancelled(true);
                    
                    // Optional: send message once every few seconds
                    if (plugin.getTimeLeft() % 20 == 0) { // Every 20 seconds
                        player.sendMessage("§6[SMP] §cPlease wait at spawn! §e" + 
                            formatTime(plugin.getTimeLeft()) + " remaining");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Teleport players to spawn if timer is running
        if (plugin.isTimerRunning()) {
            event.setRespawnLocation(event.getPlayer().getWorld().getSpawnLocation());
        }
    }
    
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
