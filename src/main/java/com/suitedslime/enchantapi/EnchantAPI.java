/**
 * *****************************************************************************
 * EnchantAPI
 *
 * EnchantAPI
 *
 * @author SuitedSlime
 * @licence Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * *****************************************************************************
 */

package com.suitedslime.enchantapi;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EnchantAPI extends JavaPlugin {
    private static Hashtable<String, CustomEnchantment> enchantments = new Hashtable();

    public void onEnable() {
        new EListener(this);

        getCommand("enchantlist").setExecutor(this);

        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if ((plugin instanceof EnchantPlugin)) ((EnchantPlugin) plugin).registerEnchantments();
        }
        for (Player player : getServer().getOnlinePlayers()) {
            EEquip.loadPlayer(player);
        }
    }

    public void onDisable() {
        HandlerList.unregisterAll();
        enchantments.clear();
        EEquip.clear();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String message = "Registered enchants: ";
        if (enchantments.size() > 0) {
            CustomEnchantment enchantment;
            for (Iterator i$ = enchantments.values().iterator(); i$.hasNext(); message = message + enchantment.name()
                    + ", ") enchantment = (CustomEnchantment)i$.next();
            message = message.substring(0, message.length() - 2);
        }
        sender.sendMessage(message);
        return true;
    }

    public static boolean isRegistered(String enchantmentName) {
        return enchantments.containsKey(enchantmentName.toUpperCase());
    }

    public static CustomEnchantment getEnchantment(String name) {
        return (CustomEnchantment) enchantments.get(name.toUpperCase());
    }

    public static Set<String> getEnchantmentNames() {
        return enchantments.keySet();
    }

    public static Collection<CustomEnchantment> getEnchantments() {
        return enchantments.values();
    }

    public static boolean registerCustomEnchantment(CustomEnchantment enchantment) {
        if (enchantments.contains(enchantment.enchantName.toUpperCase())) return false;
        enchantments.put(enchantment.enchantName.toUpperCase(), enchantment);
        return true;
    }

    public static boolean unregisterCustomEnchantment(String enchantmentName) {
        if (enchantments.containsKey(enchantmentName.toUpperCase())) {
            enchantments.remove(enchantmentName.toUpperCase());
            return true;
        }
        return false;
    }

    public static Map<CustomEnchantment, Integer> getEnchantments(ItemStack item) {
        HashMap list = new HashMap();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return list;
        if (!meta.hasLore()) return list;
        for (String lore : meta.getLore()) {
            String name = ENameParser.parseName(lore);
            int level = ENameParser.parseLevel(lore);
            if ((name != null) && (level != 0)) {
                if (isRegistered(name)) {
                    list.put(getEnchantment(name), Integer.valueOf(level));
                }
            }
        }
        return list;
    }

    public static boolean itemHasEnchantment(ItemStack item, String enchantmentName) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        if (!meta.hasLore()) return false;
        for (String lore : meta.getLore()) {
            if ((lore.contains(enchantmentName)) && (ENameParser.parseLevel(lore) > 0)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack removeEnchantments(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (!meta.hasLore()) return item;
        List lore = meta.getLore();
        for (Map.Entry entry : getEnchantments(item).entrySet()) {
            lore.remove(ChatColor.GRAY + ((CustomEnchantment) entry.getKey()).name() + " " + ERomanNumeral.numeralOf(
                    ((Integer) entry.getValue()).intValue()));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
