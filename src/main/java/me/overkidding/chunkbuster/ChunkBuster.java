package me.overkidding.chunkbuster;

import lombok.Getter;
import me.overkidding.chunkbuster.command.ChunkBusterCommand;
import me.overkidding.chunkbuster.listener.ChunkBusterListener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ChunkBuster extends JavaPlugin {

    @Getter private static ChunkBuster instance;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        getServer().getPluginManager().registerEvents(new ChunkBusterListener(), this);
        getCommand("chunkbuster").setExecutor(new ChunkBusterCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
