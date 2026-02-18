package com.example.smptimer.tasks;

import com.example.smptimer.SMPTimerPlugin;
import com.example.smptimer.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerTask extends BukkitRunnable {

    private final SMPTimerPlugin plugin;
    private BossBar bossBar;
    private int lastAnnouncement = -1;

    public TimerTask(SMPTimerPlugin plugin) {
        this.plugin = plugin;
        createBossBar();
    }

    private void createBossBar() {
        bossBar = Bukkit.createBossBar(
            MessageUtils.colorize("&6SMP Starting in..."),
            BarColor.GREEN,
            BarStyle.SEGMENTED_10
        );
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }
    }

    @Override
    public void run() {
        int timeLeft = plugin.getTimeLeft();
        
        if (timeLeft <= 0) {
            finishTimer();
            return;
        }
        
        // Update boss bar
        double progress = (double) timeLeft / (plugin.getTimeLeft() + plugin.getTimeLeft());
        bossBar.setProgress(Math.max(0, Math.min(1, progress)));
        bossBar.setTitle(MessageUtils.colorize(
            "&6SMP Starts in: &e" + MessageUtils.formatTime(timeLeft)));
        
        // Change color based on time
        if (timeLeft <= 10) {
            bossBar.setColor(BarColor.RED);
        } else if (timeLeft <= 30) {
            bossBar.setColor(BarColor.YELLOW);
        }
        
        // Special announcements
        if (timeLeft == 60 && lastAnnouncement != 60) {
            broadcastAnnouncement("&6&l1 MINUTE LEFT!", "sounds.warning");
            lastAnnouncement = 60;
        } else if (timeLeft == 30 && lastAnnouncement != 30) {
            broadcastAnnouncement("&c&l30 SECONDS LEFT!", "sounds.warning");
            lastAnnouncement = 30;
        } else if (timeLeft == 10 && lastAnnouncement != 10) {
            broadcastAnnouncement("&4&l10 SECONDS!", "sounds.tick");
            lastAnnouncement = 10;
        } else if (timeLeft <= 5 && timeLeft > 0 && lastAnnouncement != timeLeft) {
            broadcastCountdown(timeLeft);
            lastAnnouncement = timeLeft;
        }
        
        plugin.setTimeLeft(timeLeft - 1);
    }

    private void finishTimer() {
        // Cancel task
        this.cancel();
        plugin.setTaskId(-1);
        
        // Update plugin state
        plugin.setTimerRunning(false);
        plugin.setPvpEnabled(true);
        
        // Remove boss bar
        bossBar.removeAll();
        
        // Expand world border
        plugin.expandWorldBorder();
        
        // Celebration messages
        String[] messages = {
            "&6&l⚡ &e&lSMP HAS STARTED! &6&l⚡",
            "&a&lGood luck and have fun!",
            "&b&lMay the best survive!",
            "&d&lLet the adventure begin!"
        };
        
        for (String message : messages) {
            Bukkit.broadcastMessage(MessageUtils.colorize(message));
        }
        
        // Play end sound and show title
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), 
                Sound.valueOf(plugin.getPluginConfig().getString("sounds.end")), 1.0f, 1.0f);
            
            player.sendTitle(
                MessageUtils.colorize("&6&lSMP STARTED!"),
                MessageUtils.colorize("&eThe adventure begins!"),
                10, 70, 20
            );
        }
        
        // Give welcome gifts
        if (plugin.getPluginConfig().getBoolean("features.welcome-gifts")) {
            giveWelcomeGifts();
        }
    }

    private void broadcastAnnouncement(String message, String soundPath) {
        Bukkit.broadcastMessage(MessageUtils.colorize(message));
        
        String soundName = plugin.getPluginConfig().getString(soundPath);
        if (soundName != null) {
            try {
                Sound sound = Sound.valueOf(soundName);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                }
            } catch (IllegalArgumentException e) {
                // Sound not found, skip
            }
        }
    }

    private void broadcastCountdown(int seconds) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(MessageUtils.colorize("&c" + seconds + "..."));
            
            try {
                Sound sound = Sound.valueOf(plugin.getPluginConfig().getString("sounds.tick"));
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                // Sound not found, skip
            }
        }
    }

    private void giveWelcomeGifts() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (String gift : plugin.getPluginConfig().getStringList("welcome-gifts")) {
                String[] parts = gift.split(",");
                if (parts.length == 2) {
                    try {
                        org.bukkit.Material material = org.bukkit.Material.valueOf(parts[0]);
                        int amount = Integer.parseInt(parts[1]);
                        player.getInventory().addItem(new org.bukkit.inventory.ItemStack(material, amount));
                    } catch (IllegalArgumentException e) {
                        // Invalid material or amount, skip
                    }
                }
            }
        }
    }
}
