/**
 * *****************************************************************************
 * EnchantAPI
 *
 * CustomEnchantment
 *
 * @author SuitedSlime
 * @licence Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * *****************************************************************************
 */

package com.suitedslime.enchantapi;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomEnchantment {
    protected String enchantName;
    protected String[] naturalItems;

    public CustomEnchantment(String name, String[] naturalItems) {
        this.enchantName = name;
        this.naturalItems = naturalItems;
    }

    public int getEnchantmentLevel(int expLevel) {
        return 0;
    }

    public boolean canEnchantOnto(ItemStack item) {
        for (String validItem : this.naturalItems) {
            if (item.getType().name().equalsIgnoreCase(validItem)) return true;
        }
        return false;
    }

    public ItemStack addToItem(ItemStack item, int enchantLevel) {
        ItemMeta itemMeta = item.getItemMeta();
        List metaLore = itemMeta.getLore() == null ? new ArrayList() : itemMeta.getLore();

        for (String lore : metaLore) {
            if (lore.contains(this.enchantName)) {
                String loreName = ENameParser.parseName(lore);
                if ((loreName != null) && (this.enchantName.equalsIgnoreCase(loreName))) {
                    String[] pieces = lore.split(" ");
                    int level = ERomalNumeral.getValueOf(pieces[(pieces.length - 1)]);
                    if (level != 0) {
                        if (level >= enchantLevel) return item;

                        itemMeta.getLore().remove(lore);
                        break;
                    }
                }
            }
        }

        metaLore.add(0, ChatColor.GRAY + this.enchantName + " " + ERomalNumeral.numeralOf(enchantLevel));
        itemMeta.setLore(metaLore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
