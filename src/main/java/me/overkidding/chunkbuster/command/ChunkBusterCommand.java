package me.overkidding.chunkbuster.command;

import me.overkidding.chunkbuster.ChunkBuster;
import me.overkidding.chunkbuster.utils.XItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChunkBusterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("chunkbuster.give")){
            sender.sendMessage(ChatColor.RED + "Error: No permission.");
            return true;
        }
        if(args.length == 0){
            sender.sendMessage(ChatColor.RED + "Usage: /chunkbuster <give (player):reload>");
            return true;
        }
        if(args[0].equalsIgnoreCase("give")){
            if(args.length < 2){
                sender.sendMessage(ChatColor.RED + "Usage: /chunkbuster give (player)");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null){
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            ItemStack chunkBusterItem = XItemStack.deserialize(ChunkBuster.getInstance().getConfig().getConfigurationSection("ITEM"), s -> ChatColor.translateAlternateColorCodes('&', s));
            target.getInventory().addItem(chunkBusterItem);
            target.updateInventory();
        }else if(args[0].equalsIgnoreCase("reload")){
            ChunkBuster.getInstance().reloadConfig();
        }else{
            sender.sendMessage(ChatColor.RED + "Usage: /chunkbuster <give (player):reload>");
        }
        return true;
    }
}
