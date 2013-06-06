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
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Contains methods to register and access custom enchantments
 *
 * @author SuitedSlime
 * @version 0.2
 */

public class EnchantAPI extends JavaPlugin implements CommandExecutor {

    /**
     * A table of the custom enchantments that are registered
     */
    private static Hashtable<String, CustomEnchantment> enchantments = new Hashtable<String, CustomEnchantment>();

    /**
     * Enabled the plugin and calls for all custom enchantments from any plugins
     * that extend the EnchantPlugin class
     */
    public void onEnable() {

        // Listeners
        new EListener(this);
        getCommand("enchantlist").setExecutor(this);

        // Get custom enchantments from other plugins
        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            if ((plugin instanceof EnchantPlugin)) ((EnchantPlugin) plugin).registerEnchantments();
        }
        for (Player player : getServer().getOnlinePlayers()) {
            EEquip.loadPlayer(player);
        }
    }

    /**
     * Diasabled the plugin and clears all custom enchantments
     */
    public void onDisable() {
        HandlerList.unregisterAll();
        enchantments.clear();
        EEquip.clear();
    }

    /**
     * Displays the list of registered enchantments when the command /enchantlist is executed
     *
     * @param sender the sender of the command that will receive the list
     * @param cmd    the command (not used because it can only be one thing)
     * @param label  the command label (not used)
     * @param args   arguments (not used)
     *
     * @return true
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String message = "Registered enchants: ";
        if (enchantments.size() > 0) {
            for (CustomEnchantment enchantment : enchantments.values()) message += enchantment.name() + ", ";
            message = message.substring(0, message.length() - 2);
        }
        sender.sendMessage(message);
        return true;
    }

    /**
     * Checks if the enchantment with the given name is currently registered
     *
     * @param enchantmentName name of the enchantment
     *
     * @return true if registered, false if otherwise
     */
    public static boolean isRegistered(String enchantmentName) {
        return enchantments.containsKey(enchantmentName.toUpperCase());
    }

    /**
     * Retrieves the custom enchantment with the given name
     *
     * @param name name of the enchantment
     *
     * @return the enchantment with the given name, null if not found
     */
    public static CustomEnchantment getEnchantment(String name) {
        return (CustomEnchantment) enchantments.get(name.toUpperCase());
    }

    /**
     * Retrieves the name of all enchantments that have been registered
     *
     * @return set of custom enchantment names
     */
    public static Set<String> getEnchantmentNames() {
        return enchantments.keySet();
    }

    /**
     * Retrieves all custom enchantments
     *
     * @return the list of all custom enchantments
     */
    public static Collection<CustomEnchantment> getEnchantments() {
        return enchantments.values();
    }

    /**
     * Registeres the given custom enchant for the plugin
     *
     * @param enchantment the enchantment to register
     *
     * @return true if registered, false otherwise
     */
    public static boolean registerCustomEnchantment(CustomEnchantment enchantment) {
        if (enchantments.contains(enchantment.enchantName.toUpperCase())) return false;
        enchantments.put(enchantment.enchantName.toUpperCase(), enchantment);
        return true;
    }

    /**
     * Unregisters the enchantment with the given name
     *
     * @param enchantmentName name of the enchantment to unregister
     *
     * @return true if removed, false if it didn't exist
     */
    public static boolean unregisterCustomEnchantment(String enchantmentName) {
        if (enchantments.containsKey(enchantmentName.toUpperCase())) {
            enchantments.remove(enchantmentName.toUpperCase());
            return true;
        }
        return false;
    }

    /**
     * Returns the list of custom enchantments applied to the item
     *
     * @param item the item that's been checked for enchantments
     *
     * @return the list of attached enchantments
     */
    public static Map<CustomEnchantment, Integer> getEnchantments(ItemStack item) {
        HashMap<CustomEnchantment, Integer> list = new HashMap<CustomEnchantment, Integer>();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return list;
        if (!meta.hasLore()) return list;
        for (String lore : meta.getLore()) {
            String name = ENameParser.parseName(lore);
            int level = ENameParser.parseLevel(lore);
            if (name == null) continue;
            if (level == 0) continue;
            if (EnchantAPI.isRegistered(name)) {
                list.put(EnchantAPI.getEnchantment(name), level);
            }
        }
        return list;
    }

    /**
     * Checks if the given item has an enchantment with the given name
     *
     * @param item            item to check
     * @param enchantmentName name of the enchantment
     *
     * @return true if it has the enchant, false otherwise
     */
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

    /**
     * Removes all enchantments for the given item
     *
     * @param item item to clear enchantments from
     *
     * @return the item without enchantments
     */
    public static ItemStack removeEnchantments(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (!meta.hasLore()) return item;
        List<String> lore = meta.getLore();
        for (Map.Entry<CustomEnchantment, Integer> entry : getEnchantments(item).entrySet()) {
            lore.remove(ChatColor.GRAY + entry.getKey().name() + " " + ERomanNumeral.numeralOf(entry.getValue()));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
