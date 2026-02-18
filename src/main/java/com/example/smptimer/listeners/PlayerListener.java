package com.example.smptimer.listeners;

import com.example.smptimer.SMPTimerPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {

    private final SMPTimerPlugin plugin;

    public PlayerListener(SMPTimerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.isTimerRunning()) {
            // Send info to joining player
            int minutes = plugin.getTimeLeft() / 60;
            int seconds = plugin.getTimeLeft() % 60;
            event.getPlayer().sendMessage(plugin.getConfig().getString("messages.prefix") + 
                "§eSMP starts in: §a" + minutes + "m " + seconds + "s");
        } else if (plugin.isPvpEnabled()) {
            // Extra feature: MOTD when SMP is active
            event.getPlayer().sendMessage(plugin.getConfig().getString("messages.prefix") + 
                "§aWelcome to the active SMP! Good luck!");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Extra feature: Freeze players during countdown
        if (plugin.isTimerRunning() && plugin.getTimeLeft() > 0 && 
            !event.getPlayer().hasPermission("smptimer.admin")) {
            
            // Check if player is trying to move significantly
            if (event.getFrom().distance(event.getTo()) > 0.1) {
                event.setCancelled(true);
            }
        }
    }
}
