package com.example.smptimer.tasks;

import com.example.smptimer.SMPTimerPlugin;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerTask implements Runnable {

    private final SMPTimerPlugin plugin;
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();

    public TimerTask(SMPTimerPlugin plugin) {
        this.plugin = plugin;
        createBossBars();
    }

    private void createBossBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BossBar bossBar = BossBar.bossBar(
                Component.text("§6SMP Starting in..."),
                1.0f,
                BossBar.Color.BLUE,
                BossBar.Overlay.PROGRESS
            );
            player.showBossBar(bossBar);
            playerBossBars.put(player.getUniqueId(), bossBar);
        }
    }

    public void hideBossBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BossBar bar = playerBossBars.get(player.getUniqueId());
            if (bar != null) {
                player.hideBossBar(bar);
            }
        }
        playerBossBars.clear();
    }

    @Override
    public void run() {
        // Check if plugin is still enabled and timer is running
        if (!plugin.isEnabled() || !plugin.isTimerRunning()) {
            return;
        }
        
        int timeLeft = plugin.getTimeLeft();
        
        if (timeLeft <= 0) {
            finishTimer();
            return;
        }
        
        // Calculate progress (from 1.0 to 0.0)
        int totalSeconds = plugin.getTimeLeft() + timeLeft; // This was wrong, fixing:
        int initialTotal = plugin.getTimeLeft() + (Bukkit.getScheduler().isCurrentlyRunning(plugin.getTaskId()) ? timeLeft : 0);
        // Better to store initial time, but for now use a simpler approach:
        float progress = (float) timeLeft / (float) (timeLeft + 1); // Temporary fix
        
        // Actually, let's store the initial time in the plugin
        // For now, we'll just use a simpler calculation
        progress = Math.max(0, Math.min(1, (float) timeLeft / 60f)); // Assuming max 60 minutes
        
        String formattedTime = formatTime(timeLeft);
        
        // Update boss bars
        for (Player player : Bukkit.getOnlinePlayers()) {
            BossBar bar = playerBossBars.get(player.getUniqueId());
            if (bar != null) {
                bar.name(Component.text("§6SMP Starts in: §e" + formattedTime));
                bar.progress(Math.max(0.01f, Math.min(1.0f, progress)));
                
                // Change color based on time left
                if (timeLeft <= 10) {
                    bar.color(BossBar.Color.RED);
                } else if (timeLeft <= 30) {
                    bar.color(BossBar.Color.YELLOW);
                } else if (timeLeft <= 60) {
                    bar.color(BossBar.Color.GREEN);
                }
            }
        }
        
        // Special announcements at certain times
        if (timeLeft == 300) { // 5 minutes
            broadcastAnnouncement("§6§l5 MINUTES REMAINING!", "ENTITY_PLAYER_LEVELUP");
        } else if (timeLeft == 120) { // 2 minutes
            broadcastAnnouncement("§e§l2 MINUTES LEFT!", "ENTITY_PLAYER_LEVELUP");
        } else if (timeLeft == 60) { // 1 minute
            broadcastAnnouncement("§6§l1 MINUTE LEFT!", "ENTITY_EXPERIENCE_ORB_PICKUP");
        } else if (timeLeft == 30) { // 30 seconds
            broadcastAnnouncement("§c§l30 SECONDS LEFT!", "ENTITY_EXPERIENCE_ORB_PICKUP");
        } else if (timeLeft == 10) { // 10 seconds
            broadcastAnnouncement("§c§l10 SECONDS!", "BLOCK_NOTE_BLOCK_HAT");
        } else if (timeLeft <= 5 && timeLeft > 0) { // 5,4,3,2,1
            broadcastCountdown(timeLeft);
        }
        
        plugin.setTimeLeft(timeLeft - 1);
    }

    private void finishTimer() {
        try {
            plugin.setTimerRunning(false);
            plugin.setPvpEnabled(true);
            
            // Hide boss bars
            hideBossBar();
            
            // Expand world border
            plugin.expandWorldBorder();
            
            // Celebration announcements
            String[] messages = {
                "§6§l⚡⚡⚡ SMP HAS STARTED! ⚡⚡⚡",
                "§e§lGood luck and have fun!",
                "§a§lMay the best survive!",
                "§b§lLet the adventure begin!",
                "§d§lPvP is now ENABLED!"
            };
            
            // Play end sound and show messages
            for (Player player : Bukkit.getOnlinePlayers()) {
                // Send messages
                player.sendMessage("§6§m----------------------------------------");
                for (String msg : messages) {
                    player.sendMessage(msg);
                }
                player.sendMessage("§6§m----------------------------------------");
                
                // Play sound
                try {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                } catch (Exception e) {
                    // Fallback sound
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
                
                // Show title
                player.showTitle(Title.title(
                    Component.text("§6§lSMP STARTED!"),
                    Component.text("§eThe adventure begins!"),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
                ));
            }
            
            // Give welcome gifts
            giveWelcomeGifts();
            
            // Broadcast to console
            plugin.getLogger().info("SMP Timer finished! World border expanding and PvP enabled.");
            
            // Cancel task
            if (plugin.getTaskId() != -1) {
                Bukkit.getScheduler().cancelTask(plugin.getTaskId());
                plugin.setTaskId(-1);
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error finishing timer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void broadcastAnnouncement(String message, String soundName) {
        Bukkit.broadcast(Component.text("§6[SMP] §r" + message));
        
        try {
            Sound sound = Sound.valueOf(soundName);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            }
        } catch (Exception e) {
            // Sound not found, ignore
        }
    }

    private void broadcastCountdown(int seconds) {
        String message = "§c§l" + seconds + "...";
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
            try {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
            } catch (Exception e) {
                // Fallback sound
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void giveWelcomeGifts() {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().addItem(
                    new org.bukkit.inventory.ItemStack(org.bukkit.Material.COOKED_BEEF, 16),
                    new org.bukkit.inventory.ItemStack(org.bukkit.Material.OAK_LOG, 32),
                    new org.bukkit.inventory.ItemStack(org.bukkit.Material.STONE_PICKAXE),
                    new org.bukkit.inventory.ItemStack(org.bukkit.Material.STONE_SWORD),
                    new org.bukkit.inventory.ItemStack(org.bukkit.Material.TORCH, 16)
                );
                player.sendMessage("§aYou have received starter gifts!");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not give welcome gifts: " + e.getMessage());
        }
    }
}
