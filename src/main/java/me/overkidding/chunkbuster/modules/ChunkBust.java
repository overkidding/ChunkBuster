package me.overkidding.chunkbuster.modules;

import lombok.Getter;
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

    @Getter private final static FileConfiguration configuration = ChunkBuster.getInstance().getConfig();
    private static final List<Pair<Integer, Integer>> currentChunkBusts = new ArrayList<>();
    private final Map<UUID, Long> coolDowns = new HashMap<>();

    private final Player player;
    private final Location clicked;
    private final Chunk chunk;

    private long start = -1, end = -1;

    @Setter private int totalBlocks;

    @Getter private final WorkloadRunnable runnable = new WorkloadRunnable(configuration.getDouble("SETTINGS.MAX_MILLIS_PER_TICK"), this);

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
        new DistributedFiller(this).fill(chunk, Material.AIR);
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
                        .replace("%totalBlocks%", totalBlocks + "")
                        .replace("%time%", Math.max(0, TimeUnit.MILLISECONDS.toSeconds(end - start) - configuration.getInt("SETTINGS.DELAY_BEFORE_START")) + "")
        ));
    }

    public static ItemStack getItem(){
        return XItemStack.deserialize(configuration.getConfigurationSection("ITEM"), s -> ChatColor.translateAlternateColorCodes('&', s));
    }

    public int getChunkX(){
        return chunk.getX();
    }

    public int getChunkZ(){
        return chunk.getZ();
    }

    public int getDelayBeforeStart(){
        return configuration.getInt("SETTINGS.DELAY_BEFORE_START");
    }

    public boolean isPhysics(){
        return configuration.getBoolean("SETTINGS.APPLY_PHYSICS");
    }

    public int getMinHeight(){
        return configuration.getInt("SETTINGS.MIN_HEIGHT");
    }

    public int getMaxHeight(){
        return configuration.getInt("SETTINGS.MAX_HEIGHT");
    }

}
