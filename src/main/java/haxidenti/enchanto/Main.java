package haxidenti.enchanto;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Predicate;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        if (command.getName().equals("enchanto")) {
            Player player = extractPlayer(sender);
            doOperationForPlayer("Enchantment", player, Enchanto::executeEnchanting);
        } else if (command.getName().equals("deenchanto")) {
            Player player = extractPlayer(sender);
            doOperationForPlayer("DeEnchantment", player, Enchanto::removeEnchantments);
        }
        return true;
    }

    static void doOperationForPlayer(String name, Player player, Predicate<Player> pred) {
        boolean success = pred.test(player);
        if (success) {
            player.sendMessage(ChatColor.YELLOW + name + " success!");
        } else {
            player.sendMessage(ChatColor.RED + name + " failed!");
        }
    }

    static Player extractPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sorry, only player can do it!");
            return null;
        }
        return (Player) sender;
    }
}