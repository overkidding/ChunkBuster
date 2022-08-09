package me.overkidding.chunkbuster;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
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

    // declare your flag as a field accessible to other parts of your code (so you can use this to check it)
// note: if you want to use a different type of flag, make sure you change StateFlag here and below to that type
    public static StateFlag CHUNK_BUSTING_FLAG;

    @Override
    public void onLoad() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("chunk-busting", true);
            registry.register(flag);
            CHUNK_BUSTING_FLAG = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("chunk-busting");
            if (existing instanceof StateFlag) {
                CHUNK_BUSTING_FLAG = (StateFlag) existing;
            } else {
                this.getPluginLoader().disablePlugin(this);
            }
        }
    }

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
