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

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

public class EnchantAPI extends JavaPlugin {
    private static Hashtable<String, CustomEnchantment> enchantments = new Hashtable();

    public void onEnable() {
        new EListener(this);
        for (Plugin plugin : getServer().getPluginManager().getPlugins())
            if ((plugin instanceof EnchantPlugin)) ((EnchantPlugin)plugin).registerEnchantments();
    }

    public void onDisable() {
        HandlerList.unregisterAll();
        enchantments.clear();
    }

    public static boolean isRegistered(String enchantmentName) {
        return enchantments.containsKey(enchantmentName.toUpperCase());
    }

    public static CustomEnchantment getEnchantment(String name) {
        return (CustomEnchantment)enchantments.get(name.toUpperCase());
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
}
