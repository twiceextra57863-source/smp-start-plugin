package com.example.smptimer.commands;

import com.example.smptimer.SMPTimerPlugin;
import com.example.smptimer.tasks.TimerTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            sender.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }

        if (args.length < 1 || !args[0].equalsIgnoreCase("start")) {
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                              "Usage: /smp start <minutes>");
            return true;
        }

        if (plugin.isTimerRunning()) {
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                              plugin.getConfig().getString("messages.already-started"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                              plugin.getConfig().getString("messages.invalid-time"));
            return true;
        }

        try {
            int minutes = Integer.parseInt(args[1]);
            if (minutes <= 0) {
                throw new NumberFormatException();
            }
            
            startTimer(minutes);
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                              plugin.getConfig().getString("messages.timer-started")
                              .replace("{time}", String.valueOf(minutes)));
            
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                              plugin.getConfig().getString("messages.invalid-time"));
        }

        return true;
    }

    private void startTimer(int minutes) {
        plugin.setTimerRunning(true);
        plugin.setPvpEnabled(false);
        plugin.setTimeLeft(minutes * 60);
        
        // Teleport all players to spawn
        World world = Bukkit.getWorlds().getFirst();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(world.getSpawnLocation());
        }
        
        // Setup initial world border
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(plugin.getConfig().getInt("border.start-size"));
        
        // Initial announcement
        String message = "§6[SMP] §eSMP will start in §a" + minutes + " §eminutes!";
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
            player.playSound(player.getLocation(), 
                           Sound.valueOf(plugin.getConfig().getString("sounds.start")), 1.0f, 1.0f);
        }
        
        // Start timer task
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, 
            new TimerTask(plugin), 0L, 20L);
        plugin.setTaskId(taskId);
    }
}
