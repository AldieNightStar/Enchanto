package haxidenti.enchanto;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Random;

public class Enchanto {

    static Enchantment[] enchantments = Enchantment.values();
    static Random random = new Random();

    public static boolean executeEnchanting(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        Material type = item.getType();

        if (type.equals(Material.COAL) || type.equals(Material.CHARCOAL)) {
            int addLevel = (int) Math.floor(item.getAmount() / 4f);

            if (addLevel < 1) {
                return false;
            }

            player.setLevel(player.getLevel() + addLevel);

            int amt = item.getAmount() % 4;

            if (amt == 0) {
                inventory.setItemInMainHand(new ItemStack(Material.AIR));
            } else {
                item.setAmount(amt);
            }
            player.updateInventory();
            return true;
        } else if (type.equals(Material.BOOK)) {
            int cnt = item.getAmount();
            int successCnt = 0;

            for (int i = 0; i < cnt; i++) {
                ItemStack ebook = new ItemStack(Material.ENCHANTED_BOOK);
                ItemMeta meta = ebook.getItemMeta();
                if (addEnchantment(player, meta)) {
                    successCnt += 1;
                } else {
                    break;
                }
                ebook.setItemMeta(meta);

                inventory.setItemInMainHand(ebook);
                if (cnt != 1) player.dropItem(true);
            }
            if (cnt != 1) inventory.setItemInMainHand(new ItemStack(Material.BOOK, cnt - successCnt));
            player.updateInventory();

            return successCnt == cnt;
        } else if (type.equals(Material.ENCHANTED_BOOK)) {
            ItemStack item2 = inventory.getItemInOffHand();
            if (item2.getType().equals(Material.AIR)) {
                player.sendMessage(ChatColor.RED + "Cannot be AIR");
                return false;
            }
            if (item2.getAmount() != 1) {
                player.sendMessage(ChatColor.RED + "Count of items should be 1");
                return false;
            }
            ItemMeta meta2 = item2.getItemMeta();
            Map<Enchantment, Integer> enchantments2 = item2.getEnchantments();
            item.getEnchantments().forEach((enchantment, lvl) -> {
                meta2.addEnchant(enchantment, enchantments2.getOrDefault(enchantment, 0) + lvl, true);
            });
            item2.setItemMeta(meta2);

            inventory.setItemInMainHand(new ItemStack(Material.AIR));
            player.updateInventory();
            return true;
        } else if (type.equals(Material.PAPER)) {
            int cnt = (int) Math.floor(item.getAmount() / 3d);
            if (cnt < 1) {
                return false;
            }
            item.setType(Material.BOOK);
            item.setAmount(cnt);
            return true;
        } else if (type.equals(Material.LAPIS_LAZULI)) {
            int amount = item.getAmount();
            item.setAmount(0);
            item.setType(Material.AIR);
            player.setLevel(player.getLevel() + amount * 3);
            return true;
        } else if (!type.equals(Material.AIR)) {
            try {
                ItemMeta meta = item.getItemMeta();
                Damageable d = (Damageable) meta;

                int lvl = getLevel(player);
                if (lvl < 1) {
                    return false;
                }

                int damagePoints = d.getDamage();
                if (damagePoints < 1) {
                    return false;
                }
                damagePoints -= 100;
                if (damagePoints < 0) damagePoints = 0;
                d.setDamage(damagePoints);

                item.setItemMeta(meta);

                player.setLevel(lvl - 1);

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    static boolean addEnchantment(Player player, ItemMeta meta) {
        int playerLevel = getLevel(player);
        if (playerLevel < 4) {
            player.sendMessage(ChatColor.RED + "Not enough level. Need at least 4");
            return false;
        }

        playerLevel -= 4;

        int id = random.nextInt(enchantments.length);
        Enchantment enchantment = enchantments[id];
        meta.addEnchant(enchantment, meta.getEnchantLevel(enchantment) + random.nextInt(2) + 1, true);

        // Update level for player ONLY when it is not a BIG NUMBER. Otherwise it's CREATIVE
        if (playerLevel < Integer.MAX_VALUE - 100) player.setLevel(playerLevel);

        return true;
    }

    static boolean removeEnchantments(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        ItemStack offItem = inventory.getItemInOffHand();

        ItemMeta meta = offItem.getItemMeta();
        if (meta == null) {
            return false;
        }
        Map<Enchantment, Integer> enchants = meta.getEnchants();
        int enchantsSize = enchants.size();

        if (offItem.getType().equals(Material.AIR)) {
            return false;
        }
        if (!meta.hasEnchants()) {
            return false;
        }
        if (!item.getType().equals(Material.BOOK)) {
            return false;
        }
        if (item.getAmount() < enchantsSize) {
            return false;
        }
        meta.getEnchants().forEach((enchantment, lvl) -> {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            {
                ItemMeta bmeta = book.getItemMeta();
                if (bmeta == null) {
                    return;
                }
                bmeta.addEnchant(enchantment, lvl, true);
                book.setItemMeta(bmeta);
                offItem.removeEnchantment(enchantment);
            }
            dropOnLocation(player.getLocation(), book);
        });
        item.setAmount(item.getAmount() - enchantsSize);
        player.updateInventory();
        return true;
    }

    static int getLevel(Player player) {
        GameMode gm = player.getGameMode();
        if (gm.equals(GameMode.CREATIVE)) return Integer.MAX_VALUE;
        else if (gm.equals(GameMode.SURVIVAL) || gm.equals(GameMode.ADVENTURE)) return player.getLevel();
        return 0;
    }

    static void dropOnLocation(Location location, ItemStack item) {
        location.getWorld().dropItem(location, item);
    }
}
