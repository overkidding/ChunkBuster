package me.overkidding.chunkbuster.command;

import me.overkidding.chunkbuster.ChunkBuster;
import me.overkidding.chunkbuster.modules.ChunkBust;
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
        if(!sender.hasPermission("chunkbuster.admin")){
            sender.sendMessage(ChatColor.YELLOW + "This server is using ChunkBuster" + ChatColor.WHITE + " v" + ChunkBuster.getInstance().getDescription().getVersion() + " by " + ChatColor.WHITE + "overkidding");
            return true;
        }
        if(args.length == 0){
            sender.sendMessage(ChatColor.RED + "Usage: /chunkbuster <give (player):reload>");
            return true;
        }
        if(args[0].equalsIgnoreCase("give")){
            if(args.length < 2){
                sender.sendMessage(ChatColor.RED + "Usage: /chunkbuster give (player) (amount)");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null){
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            int amount = 1;
            if(args.length > 2){
                try{
                    amount = Integer.parseInt(args[2]);
                }catch (Exception ex){
                    sender.sendMessage(ChatColor.RED + "Insert a valid amount.");
                    return true;
                }
            }

            ItemStack chunkBusterItem = ChunkBust.getItem().clone();
            chunkBusterItem.setAmount(amount);
            target.getInventory().addItem(chunkBusterItem);
            target.updateInventory();
            sender.sendMessage(ChatColor.GREEN + "Gave x" + amount + " chunkbuster" + (amount == 1 ? "" : "s") + " to " + target.getName() + ".");
        }else if(args[0].equalsIgnoreCase("reload")){
            ChunkBuster.getInstance().reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Reloaded ChunkBuster.");
        }else{
            sender.sendMessage(ChatColor.RED + "Usage: /chunkbuster <give (player):reload>");
        }
        return true;
    }
}
