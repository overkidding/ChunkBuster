package me.overkidding.chunkbuster.listener;

import me.overkidding.chunkbuster.modules.ChunkBust;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ChunkBusterListener implements Listener {


    @EventHandler
    public void onUse(BlockPlaceEvent event){
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if(ChunkBust.getItem().isSimilar(item)){
            Block clickedBlock = event.getBlockAgainst();
            ChunkBust chunkBust = new ChunkBust(player, clickedBlock.getLocation(), clickedBlock.getChunk());
            chunkBust.start();
            event.setCancelled(true);
        }
    }

}
