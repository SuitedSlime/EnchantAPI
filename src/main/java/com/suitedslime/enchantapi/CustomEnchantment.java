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
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    public String name() {
        return this.enchantName;
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
        ItemMeta meta = item.getItemMeta();
        List<String> metaLore = meta.getLore() == null ? new ArrayList() : meta.getLore();

        for (String lore : metaLore) {
            if (lore.contains(this.enchantName)) {
                String loreName = ENameParser.parseName(lore);
                if ((loreName != null) && (this.enchantName.equalsIgnoreCase(loreName))) {
                    String[] pieces = lore.split(" ");
                    int level = ERomanNumeral.getValueOf(pieces[(pieces.length - 1)]);
                    if (level != 0) {
                        if (level >= enchantLevel) return item;

                        List newLore = meta.getLore();
                        newLore.remove(lore);
                        meta.setLore(newLore);
                        break;
                    }
                }
            }
        }
        metaLore.add(0, ChatColor.GRAY + this.enchantName + " " + ERomanNumeral.numeralOf(enchantLevel));
        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack removeFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (!meta.hasLore()) return item;
        List<String> metaLore = meta.getLore();

        for (String lore : metaLore)
            if (lore.contains(this.enchantName)) {
                String loreName = ENameParser.parseName(lore);
                if ((loreName != null) &&
                        (this.enchantName.equalsIgnoreCase(loreName))) {
                    List newLore = meta.getLore();
                    newLore.remove(lore);
                    meta.setLore(newLore);
                    item.setItemMeta(meta);
                    return item;
                }
            }
        return item;
    }

    public void applyEffect(LivingEntity user, LivingEntity target, int enchantLevel,
                            EntityDamageByEntityEvent event) {

    }

    public void applyDefenseEffect(LivingEntity user, LivingEntity target, int enchantLevel, EntityDamageEvent event) {

    }

    public void applyToolEffect(Player player, Block block, int enchantLevel, BlockEvent event) {

    }

    public void applyMiscEffect(Player player, int enchatLevel, PlayerInteractEvent event) {

    }

    public void applyEquipEffect(Player player, int enchantLevel) {

    }

    public void applyUnequipEfect(Player player, int enchantLevel) {

    }
}
