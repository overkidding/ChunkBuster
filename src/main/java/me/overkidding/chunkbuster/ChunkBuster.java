package me.overkidding.chunkbuster;

import lombok.Getter;
import me.overkidding.chunkbuster.command.ChunkBusterCommand;
import me.overkidding.chunkbuster.listener.ChunkBusterListener;
import me.overkidding.chunkbuster.workload.WorkloadRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ChunkBuster extends JavaPlugin {

    @Getter private static ChunkBuster instance;

    private final WorkloadRunnable workloadRunnable = new WorkloadRunnable();



    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        getServer().getPluginManager().registerEvents(new ChunkBusterListener(), this);
        getCommand("chunkbuster").setExecutor(new ChunkBusterCommand());

        Bukkit.getScheduler().runTaskTimer(this, this.workloadRunnable, 1, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
