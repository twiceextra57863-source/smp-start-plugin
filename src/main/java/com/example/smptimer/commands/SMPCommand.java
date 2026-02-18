package com.example.smptimer.commands;

import com.example.smptimer.SMPTimerPlugin;
import com.example.smptimer.tasks.TimerTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class SMPCommand implements CommandExecutor {

    private final SMPTimerPlugin plugin;

    public SMPCommand(SMPTimerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!sender.hasPermission("smptimer.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§6[SMP] §eUsage: /smp start <minutes>");
            sender.sendMessage("§6[SMP] §eExample: /smp start 5");
            return true;
        }

        if (!args[0].equalsIgnoreCase("start")) {
            sender.sendMessage("§6[SMP] §cUnknown subcommand. Use: /smp start <minutes>");
            return true;
        }

        if (plugin.isTimerRunning()) {
            sender.sendMessage("§6[SMP] §cTimer is already running! Wait for it to finish or restart server.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§6[SMP] §cPlease specify time in minutes!");
            sender.sendMessage("§6[SMP] §eExample: /smp start 5");
            return true;
        }

        try {
            int minutes = Integer.parseInt(args[1]);
            if (minutes <= 0 || minutes > 60) {
                sender.sendMessage("§6[SMP] §cPlease enter a valid time between 1 and 60 minutes!");
                return true;
            }
            
            startTimer(minutes, sender);
            
        } catch (NumberFormatException e) {
            sender.sendMessage("§6[SMP] §cInvalid number! Please enter a valid number of minutes.");
        }

        return true;
    }

    private void startTimer(int minutes, CommandSender sender) {
        try {
            int totalSeconds = minutes * 60;
            plugin.setTimerRunning(true);
            plugin.setPvpEnabled(false);
            plugin.setTimeLeft(totalSeconds);
            
            // Teleport all players to spawn
            World world = Bukkit.getWorlds().getFirst();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(world.getSpawnLocation());
                player.sendMessage("§6[SMP] §eYou have been teleported to spawn for SMP start!");
            }
            
            // Setup initial world border
            WorldBorder border = world.getWorldBorder();
            border.setCenter(0, 0);
            border.setSize(20); // 20x20 border
            border.setWarningDistance(0);
            
            // Initial announcement with sound
            String message = "§6§l⚡ SMP COUNTDOWN ⚡";
            String message2 = "§eSMP will start in §a" + minutes + " §eminute" + (minutes > 1 ? "s" : "") + "!";
            String message3 = "§7Get ready at spawn!";
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("§6§m----------------------------------------");
                player.sendMessage(message);
                player.sendMessage(message2);
                player.sendMessage(message3);
                player.sendMessage("§6§m----------------------------------------");
                
                try {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                } catch (Exception e) {
                    // Sound might not exist in all versions, ignore
                }
            }
            
            // Also send to console
            sender.sendMessage("§6[SMP] §aTimer started for §e" + minutes + " §aminutes!");
            plugin.getLogger().info("SMP Timer started for " + minutes + " minutes");
            
            // Create and start timer task
            TimerTask timerTask = new TimerTask(plugin);
            plugin.setCurrentTimerTask(timerTask);
            
            // Start repeating task (20 ticks = 1 second)
            int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, timerTask, 0L, 20L);
            plugin.setTaskId(taskId);
            
        } catch (Exception e) {
            sender.sendMessage("§6[SMP] §cError starting timer: " + e.getMessage());
            plugin.getLogger().severe("Error starting timer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
