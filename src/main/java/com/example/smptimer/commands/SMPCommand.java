package com.example.smptimer.commands;

import com.example.smptimer.SMPTimerPlugin;
import com.example.smptimer.tasks.TimerTask;
import com.example.smptimer.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SMPCommand implements CommandExecutor, TabCompleter {

    private final SMPTimerPlugin plugin;
    private final FileConfiguration config;

    public SMPCommand(SMPTimerPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getPluginConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("smptimer.admin")) {
            sender.sendMessage(MessageUtils.colorize(
                config.getString("messages.prefix") + 
                config.getString("messages.no-permission")));
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                return handleStart(sender, args);
            case "cancel":
                return handleCancel(sender);
            case "status":
                return handleStatus(sender);
            case "setspawn":
                return handleSetSpawn(sender);
            case "reload":
                return handleReload(sender);
            default:
                sendHelpMessage(sender);
                return true;
        }
    }

    private boolean handleStart(CommandSender sender, String[] args) {
        if (plugin.isTimerRunning()) {
            sender.sendMessage(MessageUtils.colorize(
                config.getString("messages.prefix") + 
                config.getString("messages.timer-already-running")));
            return true;
        }

        int minutes;
        if (args.length < 2) {
            minutes = config.getInt("timer.default-time");
        } else {
            try {
                minutes = Integer.parseInt(args[1]);
                int maxTime = config.getInt("timer.max-time");
                
                if (minutes < 1 || minutes > maxTime) {
                    sender.sendMessage(MessageUtils.colorize(
                        config.getString("messages.prefix") + 
                        config.getString("messages.invalid-time")
                        .replace("{max}", String.valueOf(maxTime))));
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtils.colorize(
                    config.getString("messages.prefix") + 
                    config.getString("messages.invalid-time")
                    .replace("{max}", String.valueOf(config.getInt("timer.max-time")))));
                return true;
            }
        }

        startTimer(minutes);
        
        sender.sendMessage(MessageUtils.colorize(
            config.getString("messages.prefix") + 
            config.getString("messages.timer-started")
            .replace("{time}", String.valueOf(minutes))));
        
        return true;
    }

    private boolean handleCancel(CommandSender sender) {
        if (!plugin.isTimerRunning()) {
            sender.sendMessage(MessageUtils.colorize(
                config.getString("messages.prefix") + 
                config.getString("messages.timer-not-running")));
            return true;
        }

        cancelTimer();
        
        sender.sendMessage(MessageUtils.colorize(
            config.getString("messages.prefix") + 
            config.getString("messages.timer-cancelled")));
        
        return true;
    }

    private boolean handleStatus(CommandSender sender) {
        if (!plugin.isTimerRunning()) {
            sender.sendMessage(MessageUtils.colorize(
                config.getString("messages.prefix") + 
                "&cNo timer is currently running."));
            return true;
        }

        String timeFormatted = MessageUtils.formatTime(plugin.getTimeLeft());
        String progressBar = MessageUtils.getProgressBar(
            plugin.getTimeLeft(), 
            plugin.getTimeLeft() + plugin.getTimeLeft(), 
            20, 
            "■"
        );

        sender.sendMessage(MessageUtils.colorize(
            "&6=== SMP Timer Status ==="));
        sender.sendMessage(MessageUtils.colorize(
            "&eTime Remaining: &a" + timeFormatted));
        sender.sendMessage(MessageUtils.colorize(
            "&eProgress: " + progressBar));
        sender.sendMessage(MessageUtils.colorize(
            "&ePvP Status: " + (plugin.isPvpEnabled() ? "&aEnabled" : "&cDisabled")));
        
        return true;
    }

    private boolean handleSetSpawn(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize(
                config.getString("messages.prefix") + 
                "&cOnly players can set spawn location!"));
            return true;
        }

        Player player = (Player) sender;
        plugin.setSpawnLocation(player.getLocation());
        
        player.sendMessage(MessageUtils.colorize(
            config.getString("messages.prefix") + 
            config.getString("messages.spawn-set")));
        
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(MessageUtils.colorize(
            config.getString("messages.prefix") + 
            "&aConfiguration reloaded!"));
        return true;
    }

    private void startTimer(int minutes) {
        plugin.setTimerRunning(true);
        plugin.setPvpEnabled(false);
        plugin.setTimeLeft(minutes * 60);
        
        // Teleport all players
        plugin.teleportAllPlayers();
        
        // Setup initial world border
        plugin.setupInitialWorldBorder();
        
        // Initial broadcast
        String message = config.getString("messages.timer-started")
            .replace("{time}", String.valueOf(minutes));
        Bukkit.broadcastMessage(MessageUtils.colorize(
            config.getString("messages.prefix") + message));
        
        // Start timer task
        TimerTask timerTask = new TimerTask(plugin);
        int taskId = timerTask.runTaskTimer(plugin, 0L, 20L).getTaskId();
        plugin.setTaskId(taskId);
    }

    private void cancelTimer() {
        if (plugin.getTaskId() != -1) {
            Bukkit.getScheduler().cancelTask(plugin.getTaskId());
            plugin.setTaskId(-1);
        }
        
        plugin.setTimerRunning(false);
        plugin.setPvpEnabled(false);
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(MessageUtils.colorize(
            "&6=== SMP Timer Commands ==="));
        sender.sendMessage(MessageUtils.colorize(
            "&e/smp start [minutes] &7- Start the timer"));
        sender.sendMessage(MessageUtils.colorize(
            "&e/smp cancel &7- Cancel current timer"));
        sender.sendMessage(MessageUtils.colorize(
            "&e/smp status &7- Check timer status"));
        sender.sendMessage(MessageUtils.colorize(
            "&e/smp setspawn &7- Set spawn location"));
        sender.sendMessage(MessageUtils.colorize(
            "&e/smp reload &7- Reload configuration"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, 
                                      String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!sender.hasPermission("smptimer.admin")) {
            return completions;
        }

        if (args.length == 1) {
            String[] subcommands = {"start", "cancel", "status", "setspawn", "reload"};
            for (String subcmd : subcommands) {
                if (subcmd.startsWith(args[0].toLowerCase())) {
                    completions.add(subcmd);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            for (int i = 5; i <= 60; i += 5) {
                completions.add(String.valueOf(i));
            }
        }
        
        return completions;
    }
}
