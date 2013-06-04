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

import java.util.Hashtable;

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
}
