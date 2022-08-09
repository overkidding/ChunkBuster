package me.overkidding.chunkbuster.listener;

import me.overkidding.chunkbuster.ChunkBuster;
import me.overkidding.chunkbuster.utils.XItemStack;
import me.overkidding.chunkbuster.workload.filler.impl.DistributedFiller;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChunkBusterListener implements Listener {

    @EventHandler
    public void onUse(PlayerInteractEvent event){
        if(event.getItem() == null) return;
        if(!event.getAction().name().endsWith("_BLOCK")) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        ItemStack chunkBusterItem = XItemStack.deserialize(ChunkBuster.getInstance().getConfig().getConfigurationSection("ITEM"), s -> ChatColor.translateAlternateColorCodes('&', s));

        if(chunkBusterItem.isSimilar(item)){
            if(event.getClickedBlock() == null){
                player.sendMessage(ChatColor.RED + "You need to click on a block to let chunk buster do his work!");
                return;
            }
            event.setCancelled(true);
            ItemStack handItem = player.getItemInHand();
            handItem.setAmount(handItem.getAmount() - 1);
            player.setItemInHand(handItem);
            Chunk chunk = event.getClickedBlock().getChunk();
            new DistributedFiller(ChunkBuster.getInstance().getWorkloadRunnable()).fill(chunk, Material.AIR);
        }
    }

}
