package com.example.smptimer;

import com.example.smptimer.commands.SMPCommand;
import com.example.smptimer.listeners.PvPListener;
import com.example.smptimer.listeners.PlayerListener;
import com.example.smptimer.tasks.TimerTask;
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
    private TimerTask currentTimerTask = null;

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
        getLogger().info("Use /smp start <minutes> to begin!");
    }

    @Override
    public void onDisable() {
        stopTimer();
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
    
    public TimerTask getCurrentTimerTask() {
        return currentTimerTask;
    }
    
    public void setCurrentTimerTask(TimerTask task) {
        this.currentTimerTask = task;
    }

    public void stopTimer() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        if (currentTimerTask != null) {
            currentTimerTask.hideBossBar();
            currentTimerTask = null;
        }
        isTimerRunning = false;
    }

    public void setupInitialWorldBorder() {
        try {
            World world = Bukkit.getWorlds().getFirst();
            WorldBorder border = world.getWorldBorder();
            border.setCenter(0, 0);
            border.setSize(getConfig().getDouble("border.start-size", 20));
            getLogger().info("World border set to 20x20 at center");
        } catch (Exception e) {
            getLogger().warning("Failed to set world border: " + e.getMessage());
        }
    }

    public void expandWorldBorder() {
        try {
            World world = Bukkit.getWorlds().getFirst();
            WorldBorder border = world.getWorldBorder();
            double endSize = getConfig().getDouble("border.end-size", 20000);
            long expandTime = getConfig().getLong("border.expand-time", 60);
            
            border.setSize(endSize, expandTime);
            getLogger().info("World border expanding to " + (int)endSize + "x" + (int)endSize);
        } catch (Exception e) {
            getLogger().warning("Failed to expand world border: " + e.getMessage());
        }
    }
}
