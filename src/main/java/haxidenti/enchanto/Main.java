package haxidenti.enchanto;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
            if (!(sender instanceof Player)) {
                sender.sendMessage("Sorry, only player can do it!");
                return false;
            }
            Player player = (Player) sender;

            String arg = args.length > 0 ? args[0] : null;
            boolean success = Enchanto.executeEnchanting(player);
            if (success) {
                player.sendMessage(ChatColor.YELLOW + "Enchantment success!");
            } else {
                player.sendMessage(ChatColor.RED + "Enchanment falied!");
            }
        }
        return true;
    }
}