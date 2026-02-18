package com.example.smptimer.tasks;

import com.example.smptimer.SMPTimerPlugin;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;

public class TimerTask implements Runnable {

    private final SMPTimerPlugin plugin;
    private BossBar bossBar;

    public TimerTask(SMPTimerPlugin plugin) {
        this.plugin = plugin;
        createBossBar();
    }

    private void createBossBar() {
        bossBar = BossBar.bossBar(
            Component.text("SMP Starting in..."),
            1.0f,
            BossBar.Color.GREEN,
            BossBar.Overlay.PROGRESS
        );
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showBossBar(bossBar);
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
        float progress = (float) timeLeft / (plugin.getTimeLeft() + plugin.getTimeLeft());
        bossBar.progress(Math.max(0, progress));
        bossBar.name(Component.text("§6SMP Starts in: §e" + formatTime(timeLeft)));
        
        // Special announcements at certain times
        if (timeLeft == 60) {
            broadcastAnnouncement("§6§l1 MINUTE LEFT!", "entity.player.levelup");
        } else if (timeLeft == 30) {
            broadcastAnnouncement("§c§l30 SECONDS LEFT!", "entity.player.levelup");
        } else if (timeLeft == 10) {
            broadcastAnnouncement("§4§l10 SECONDS!", "block.note_block.hat");
        } else if (timeLeft <= 5 && timeLeft > 0) {
            broadcastCountdown(timeLeft);
        }
        
        plugin.setTimeLeft(timeLeft - 1);
    }

    private void finishTimer() {
        plugin.setTimerRunning(false);
        plugin.setPvpEnabled(true);
        
        // Hide boss bar
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.hideBossBar(bossBar);
        }
        
        // Expand world border
        plugin.expandWorldBorder();
        
        // Celebration announcements
        String[] messages = {
            "§6§l⚡ SMP HAS STARTED! ⚡",
            "§e§lGood luck and have fun!",
            "§a§lMay the best survive!",
            "§b§lLet the adventure begin!"
        };
        
        for (String message : messages) {
            Bukkit.broadcast(Component.text(message));
        }
        
        // Play end sound
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), 
                           Sound.valueOf(plugin.getConfig().getString("sounds.end")), 1.0f, 1.0f);
            player.showTitle(Title.title(
                Component.text("§6§lSMP STARTED!"),
                Component.text("§eThe adventure begins!"),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
            ));
        }
        
        // Extra feature: Give welcome gifts
        giveWelcomeGifts();
        
        // Cancel task
        Bukkit.getScheduler().cancelTask(plugin.getTaskId());
        plugin.setTaskId(-1);
    }

    private void broadcastAnnouncement(String message, String sound) {
        Bukkit.broadcast(Component.text(message));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), 
                           Sound.valueOf(sound), 1.0f, 1.0f);
        }
    }

    private void broadcastCountdown(int seconds) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("§c" + seconds + "...");
            player.playSound(player.getLocation(), 
                           Sound.valueOf(plugin.getConfig().getString("sounds.tick")), 1.0f, 1.0f);
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void giveWelcomeGifts() {
        // Extra feature: Give players starter items
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(
                new org.bukkit.inventory.ItemStack(org.bukkit.Material.COOKED_BEEF, 16),
                new org.bukkit.inventory.ItemStack(org.bukkit.Material.OAK_LOG, 32),
                new org.bukkit.inventory.ItemStack(org.bukkit.Material.STONE_PICKAXE, 1),
                new org.bukkit.inventory.ItemStack(org.bukkit.Material.STONE_SWORD, 1)
            );
        }
    }
}
