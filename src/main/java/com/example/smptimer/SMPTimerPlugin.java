package com.example.smptimer;

import com.example.smptimer.commands.SMPCommand;
import com.example.smptimer.listeners.PvPListener;
import com.example.smptimer.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;

public class SMPTimerPlugin extends JavaPlugin {
    
    private static SMPTimerPlugin instance;
    private boolean isTimerRunning = false;
    private int timeLeft = 0;
    private int taskId = -1;
    private boolean pvpEnabled = false;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        // Register command
        getCommand("smp").setExecutor(new SMPCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PvPListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        getLogger().info("SMPTimerPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        getLogger().info("SMPTimerPlugin has been disabled!");
    }

    public static SMPTimerPlugin getInstance() {
        return instance;
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        isTimerRunning = timerRunning;
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

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public void setupInitialWorldBorder() {
        World world = Bukkit.getWorlds().getFirst();
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(getConfig().getInt("border.start-size"));
    }

    public void expandWorldBorder() {
        World world = Bukkit.getWorlds().getFirst();
        WorldBorder border = world.getWorldBorder();
        border.setSize(getConfig().getInt("border.end-size"), 
                      getConfig().getLong("border.expand-time"));
    }
}
