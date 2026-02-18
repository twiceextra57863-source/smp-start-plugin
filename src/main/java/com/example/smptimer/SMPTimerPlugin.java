package com.example.smptimer;

import com.example.smptimer.commands.SMPCommand;
import com.example.smptimer.listeners.PvPListener;
import com.example.smptimer.listeners.PlayerListener;
import com.example.smptimer.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SMPTimerPlugin extends JavaPlugin {
    
    private static SMPTimerPlugin instance;
    private boolean timerRunning = false;
    private boolean pvpEnabled = false;
    private int timeLeft = 0;
    private int taskId = -1;
    private Location spawnLocation;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        config = getConfig();
        
        // Set default spawn location
        spawnLocation = Bukkit.getWorlds().getFirst().getSpawnLocation();
        
        // Register command
        getCommand("smp").setExecutor(new SMPCommand(this));
        
        // Register listeners
        Bukkit.getPluginManager().registerEvents(new PvPListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Check for updates (optional)
        checkForUpdates();
        
        getLogger().info("SMPTimerPlugin v" + getDescription().getVersion() + " enabled!");
        getLogger().info("Compatible with Paper/Spigot 1.21 - 1.21.1");
    }

    @Override
    public void onDisable() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        
        // Save data if needed
        if (config.getBoolean("features.auto-save")) {
            saveConfig();
        }
        
        getLogger().info("SMPTimerPlugin disabled!");
    }

    public static SMPTimerPlugin getInstance() {
        return instance;
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        this.timerRunning = timerRunning;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
        this.spawnLocation.getWorld().setSpawnLocation(spawnLocation);
    }

    public void setupInitialWorldBorder() {
        World world = Bukkit.getWorlds().getFirst();
        WorldBorder border = world.getWorldBorder();
        
        border.setCenter(config.getInt("border.center.x"), 
                        config.getInt("border.center.z"));
        border.setSize(config.getInt("border.start-size"));
    }

    public void expandWorldBorder() {
        World world = Bukkit.getWorlds().getFirst();
        WorldBorder border = world.getWorldBorder();
        
        border.setSize(config.getInt("border.end-size"), 
                      config.getLong("border.expand-time"));
    }

    public void teleportAllPlayers() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(spawnLocation);
            player.sendMessage(MessageUtils.colorize(config.getString("messages.players-teleported")));
        }
    }

    private void checkForUpdates() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Optional: Add update checker
            }
        }.runTaskTimerAsynchronously(this, 0L, 72000L);
    }

    public FileConfiguration getPluginConfig() {
        return config;
    }
}
