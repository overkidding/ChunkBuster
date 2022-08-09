package me.overkidding.chunkbuster.modules;

import lombok.RequiredArgsConstructor;
import me.overkidding.chunkbuster.ChunkBuster;
import me.overkidding.chunkbuster.events.ChunkBustPreStartEvent;
import me.overkidding.chunkbuster.utils.XItemStack;
import me.overkidding.chunkbuster.workload.WorkloadRunnable;
import me.overkidding.chunkbuster.workload.filler.impl.DistributedFiller;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ChunkBust {

    private static final FileConfiguration configuration = ChunkBuster.getInstance().getConfig();
    private final Map<UUID, Long> coolDowns = new HashMap<>();

    private final Player player;
    private final Location clicked;
    private final Chunk chunk;

    private final WorkloadRunnable runnable = new WorkloadRunnable(this);

    public void start(){
        ChunkBustPreStartEvent preStartEvent = new ChunkBustPreStartEvent(player, clicked, chunk);
        Bukkit.getPluginManager().callEvent(preStartEvent);
        if(preStartEvent.isCancelled()){
            player.sendMessage(ChatColor.RED + "Chunk busting has been cancelled!");
            player.updateInventory();
            return;
        }

        long timeLeft = coolDowns.getOrDefault(player.getUniqueId(), -1L) - System.currentTimeMillis();
        if(timeLeft > 0){
            long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configuration.getString("SETTINGS.MESSAGES.COOLDOWN")
                            .replace("%cooldown%", String.valueOf(secondsLeft))
                            .replace("%context%", secondsLeft == 1 ? "" : "s")
            ));
        }
        int coolDown = configuration.getInt("SETTINGS.COOLDOWN");
        coolDowns.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(coolDown));
        ItemStack handItem = player.getItemInHand();
        handItem.setAmount(handItem.getAmount() - 1);
        player.setItemInHand(handItem);
        new DistributedFiller(runnable, configuration).fill(chunk, Material.AIR);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getString("SETTINGS.MESSAGES.START")));
    }

    public void end(){
        runnable.cancel();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configuration.getString("SETTINGS.MESSAGES.FINISH")
                        .replace("%x%", clicked.getBlockX()+"")
                        .replace("%z%", clicked.getBlockZ() + "")
        ));
    }

    public static ItemStack getItem(){
        return XItemStack.deserialize(configuration.getConfigurationSection("ITEM"), s -> ChatColor.translateAlternateColorCodes('&', s));
    }

}
