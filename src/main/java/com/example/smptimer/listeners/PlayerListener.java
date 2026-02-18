package com.example.smptimer.listeners;

import com.example.smptimer.SMPTimerPlugin;
import com.example.smptimer.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
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
            // Send timer info to joining player
            String timeFormatted = MessageUtils.formatTime(plugin.getTimeLeft());
            String motd = plugin.getPluginConfig().getString("motd.timer-running")
                .replace("{time}", timeFormatted);
            
            player.sendMessage(MessageUtils.colorize(
                plugin.getPluginConfig().getString("messages.prefix") + motd));
            
            // Teleport to spawn if feature enabled
            if (plugin.getPluginConfig().getBoolean("features.freeze-players")) {
                player.teleport(plugin.getSpawnLocation());
            }
            
        } else if (plugin.isPvpEnabled()) {
            // MOTD when SMP is active
            String motd = plugin.getPluginConfig().getString("motd.smp-active");
            player.sendMessage(MessageUtils.colorize(
                plugin.getPluginConfig().getString("messages.prefix") + motd));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Freeze players during countdown (except admins with bypass)
        if (plugin.isTimerRunning() && 
            plugin.getPluginConfig().getBoolean("features.freeze-players") &&
            !player.hasPermission("smptimer.bypass")) {
            
            Location from = event.getFrom();
            Location to = event.getTo();
            
            if (to != null && (from.getX() != to.getX() || 
                               from.getY() != to.getY() || 
                               from.getZ() != to.getZ())) {
                event.setCancelled(true);
            }
        }
        
        // Border warning
        if (plugin.getPluginConfig().getBoolean("border-warnings.enabled") &&
            plugin.isPvpEnabled()) {
            
            checkBorderWarning(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Respawn players at spawn during timer
        if (plugin.isTimerRunning()) {
            event.setRespawnLocation(plugin.getSpawnLocation());
        }
    }

    private void checkBorderWarning(Player player) {
        WorldBorder border = player.getWorld().getWorldBorder();
        Location loc = player.getLocation();
        
        double borderSize = border.getSize() / 2;
        Location center = border.getCenter();
        
        double dx = Math.abs(loc.getX() - center.getX());
        double dz = Math.abs(loc.getZ() - center.getZ());
        double distanceFromCenter = Math.max(dx, dz);
        double distanceToBorder = borderSize - distanceFromCenter;
        
        for (int warningDist : plugin.getPluginConfig()
                .getIntegerList("border-warnings.distances")) {
            
            if (distanceToBorder <= warningDist && distanceToBorder > warningDist - 1) {
                String warningMsg = plugin.getPluginConfig()
                    .getString("border-warnings.message")
                    .replace("{blocks}", String.valueOf((int) distanceToBorder));
                
                player.sendMessage(MessageUtils.colorize(warningMsg));
                break;
            }
        }
    }
}
