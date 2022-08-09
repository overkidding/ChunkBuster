package me.overkidding.chunkbuster.listener;

import me.overkidding.chunkbuster.modules.ChunkBust;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
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

        if(ChunkBust.getItem().isSimilar(item)){
            Block clickedBlock = event.getClickedBlock();
            if(clickedBlock == null){
                player.sendMessage(ChatColor.RED + "You need to click on a block to let chunk buster do his work!");
                return;
            }

            ChunkBust chunkBust = new ChunkBust(player, clickedBlock.getLocation(), clickedBlock.getChunk());
            chunkBust.start();
            event.setCancelled(true);
        }
    }

}
