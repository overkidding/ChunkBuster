package me.overkidding.chunkbuster.modules;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.overkidding.chunkbuster.ChunkBuster;
import me.overkidding.chunkbuster.events.ChunkBustPreStartEvent;
import me.overkidding.chunkbuster.utils.Pair;
import me.overkidding.chunkbuster.utils.XItemStack;
import me.overkidding.chunkbuster.workload.WorkloadRunnable;
import me.overkidding.chunkbuster.workload.filler.impl.DistributedFiller;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ChunkBust {

    @Setter private static FileConfiguration configuration = ChunkBuster.getInstance().getConfig();
    private static final List<Pair<Integer, Integer>> currentChunkBusts = new ArrayList<>();
    private final Map<UUID, Long> coolDowns = new HashMap<>();

    private final Player player;
    private final Location clicked;
    private final Chunk chunk;

    private long start = -1, end = -1;

    private final WorkloadRunnable runnable = new WorkloadRunnable(this);

    public void start(){
        ChunkBustPreStartEvent preStartEvent = new ChunkBustPreStartEvent(player, clicked, chunk);
        Bukkit.getPluginManager().callEvent(preStartEvent);
        if(preStartEvent.isCancelled()){
            player.sendMessage(ChatColor.RED + "Chunk busting has been cancelled!");
            player.updateInventory();
            return;
        }

        if(currentChunkBusts.contains(new Pair<>(getChunkX(), getChunkZ()))){
            player.sendMessage(ChatColor.RED + "A chunk bust is already starting in this area!");
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
        currentChunkBusts.add(new Pair<>(getChunkX(), getChunkZ()));
        int coolDown = configuration.getInt("SETTINGS.COOLDOWN");
        coolDowns.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(coolDown));
        ItemStack handItem = player.getItemInHand();
        handItem.setAmount(handItem.getAmount() - 1);
        player.setItemInHand(handItem);
        start = System.currentTimeMillis();
        new DistributedFiller(runnable, configuration).fill(chunk, Material.AIR);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getString("SETTINGS.MESSAGES.START")));
    }

    public void end(){
        runnable.cancel();
        end = System.currentTimeMillis();
        currentChunkBusts.remove(new Pair<>(getChunkX(), getChunkZ()));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                configuration.getString("SETTINGS.MESSAGES.FINISH")
                        .replace("%x%", clicked.getBlockX()+"")
                        .replace("%z%", clicked.getBlockZ() + "")
                        .replace("%time%", TimeUnit.MILLISECONDS.toSeconds(end - start) + "")
        ));
    }

    public static ItemStack getItem(){
        return XItemStack.deserialize(configuration.getConfigurationSection("ITEM"), s -> ChatColor.translateAlternateColorCodes('&', s));
    }

    public int getChunkX(){
        return (chunk.getX() << 4) - 1;
    }

    public int getChunkZ(){
        return (chunk.getZ() << 4) - 1;
    }

}
