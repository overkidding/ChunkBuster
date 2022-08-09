package me.overkidding.chunkbuster.listener;

import me.overkidding.chunkbuster.ChunkBuster;
import me.overkidding.chunkbuster.events.ChunkBustPreStartEvent;
import me.overkidding.chunkbuster.utils.XItemStack;
import me.overkidding.chunkbuster.workload.filler.impl.DistributedFiller;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChunkBusterListener implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onUse(PlayerInteractEvent event){
        if(event.getItem() == null) return;
        if(!event.getAction().name().endsWith("_BLOCK")) return;

        FileConfiguration configuration = ChunkBuster.getInstance().getConfig();

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        ItemStack chunkBusterItem = XItemStack.deserialize(configuration.getConfigurationSection("ITEM"), s -> ChatColor.translateAlternateColorCodes('&', s));

        if(chunkBusterItem.isSimilar(item)){
            Block clickedBlock = event.getClickedBlock();
            if(clickedBlock == null){
                player.sendMessage(ChatColor.RED + "You need to click on a block to let chunk buster do his work!");
                return;
            }

            Chunk chunk = clickedBlock.getChunk();

            ChunkBustPreStartEvent preStartEvent = new ChunkBustPreStartEvent(player, clickedBlock.getLocation(), chunk);
            Bukkit.getPluginManager().callEvent(preStartEvent);
            if(preStartEvent.isCancelled()){
                player.sendMessage(ChatColor.RED + "Chunk busting has been cancelled!");
                return;
            }

            long timeLeft = cooldowns.getOrDefault(player.getUniqueId(), -1L) - System.currentTimeMillis();
            if(timeLeft > 0){
                long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configuration.getString("SETTINGS.MESSAGES.COOLDOWN")
                        .replace("%cooldown%", String.valueOf(secondsLeft))
                        .replace("%context%", secondsLeft == 1 ? "" : "s")
                ));
                return;
            }
            int coolDown = configuration.getInt("SETTINGS.COOLDOWN");
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(coolDown));
            event.setCancelled(true);
            ItemStack handItem = player.getItemInHand();
            handItem.setAmount(handItem.getAmount() - 1);
            player.setItemInHand(handItem);
            new DistributedFiller(ChunkBuster.getInstance().getWorkloadRunnable()).fill(chunk, Material.AIR);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configuration.getString("SETTINGS.MESSAGES.START")
            ));
        }
    }

}
