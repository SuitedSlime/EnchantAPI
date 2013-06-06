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

/**
 * Base class for all custom enchantments
 */

public abstract class CustomEnchantment {

    /**
     * Name of the enchantment
     */
    protected String enchantName;

    /**
     * Name of all the items that can receive this enchantment at the enchantment table
     */
    protected String[] naturalItems;

    /**
     * Creates a new custom enchantment with the given name that can be
     * enchanted onto the items using an enchantment table with the names
     * given in the array
     *
     * @param name         the unique name of the enchantment
     * @param naturalItems the names of the items that can normally have this enchantment
     */
    public CustomEnchantment(String name, String[] naturalItems) {
        this.enchantName = name;
        this.naturalItems = naturalItems;
    }

    /**
     * Retrieves the name of the enchantment
     *
     * @return enchantment name
     */
    public String name() {
        return this.enchantName;
    }

    /**
     * Calculates an enchantment level for this enchantment depending
     * on the EXP level of the player during an enchantment
     *
     * @param expLevel the experience level the player used
     *
     * @return returns the enchantment level; returns <1 if the enchantment should not be applied
     */
    public int getEnchantmentLevel(int expLevel) {
        return 0;
    }

    /**
     * Checks if this enchantment can be normally applied to the item
     *
     * @param item the item tp check for
     *
     * @return true if the enchantment can be normally applied, false otherwise
     */
    public boolean canEnchantOnto(ItemStack item) {
        for (String validItem : this.naturalItems) {
            if (item.getType().name().equalsIgnoreCase(validItem)) return true;
        }
        return false;
    }

    /**
     * Adds this enchantment onto the given item with the enchantment level provided
     *
     * @param item         the item being enchanted
     * @param enchantLevel the level of enchantment
     *
     * @return the enchanted item
     */
    public ItemStack addToItem(ItemStack item, int enchantLevel) {
        ItemMeta meta = item.getItemMeta();
        List<String> metaLore = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();

        for (String lore : metaLore) {
            if (lore.contains(this.enchantName)) {

                // Confirm that the enchanting name is the same
                String loreName = ENameParser.parseName(lore);
                if (loreName == null) continue;
                if (!enchantName.equalsIgnoreCase(loreName)) continue;

                // Compare the enchantment levels
                String[] pieces = lore.split(" ");
                int level = ERomanNumeral.getValueOf(pieces[pieces.length - 1]);
                if (level == 0) continue;

                // Leave higher enchantments alone
                if (level >= enchantLevel) return item;

                // Replace lower enchantments
                List<String> newLore = meta.getLore();
                newLore.remove(lore);
                meta.setLore(newLore);
                break;
            }
        }

        // Add the enchantment
        metaLore.add(0, ChatColor.GRAY + this.enchantName + " " + ERomanNumeral.numeralOf(enchantLevel));
        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Removes this enchantment from the item if it exists
     *
     * @param item item to remove this enchantment from
     *
     * @return the item without this enchantmet
     */
    public ItemStack removeFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (!meta.hasLore()) return item;
        List<String> metaLore = meta.getLore();

        // Make sure the enchantment doesn't already exist on the item
        for (String lore : metaLore)
            if (lore.contains(this.enchantName)) {

                // Confirm that the enchanting name is the same
                String loreName = ENameParser.parseName(lore);
                if (loreName == null) continue;
                if (!enchantName.equalsIgnoreCase(loreName)) continue;

                //Compare the enchantment levels
                List<String> newLore = meta.getLore();
                newLore.remove(lore);
                meta.setLore(newLore);
                item.setItemMeta(meta);
                return item;
            }
        return item;
    }

    /**
     * Applies the enchantment affect when attacking someone
     *
     * @param user         the entity that has the enchantment
     * @param target       the entity that was struck by the enchantment
     * @param enchantLevel the level of the used enchantment
     * @param event        the event details
     */
    public void applyEffect(LivingEntity user, LivingEntity target, int enchantLevel,
                            EntityDamageByEntityEvent event) {

    }

    /**
     * Applies the enchantment defensively (when taking damage)
     *
     * @param user         the entity that has the enchantment
     * @param target       the entity that attacked the enchantment, can be null
     * @param enchantLevel the level of the used enchantment
     * @param event        the event details (EntityDamageByEntityEvent, EntityDamageByBlockEvent, or just EntityDamageEvent)
     */
    public void applyDefenseEffect(LivingEntity user, LivingEntity target, int enchantLevel, EntityDamageEvent event) {

    }

    /**
     * Applies the effects while breaking blocks
     *
     * @param player       the player with the enchantment
     * @param block        the block being broken
     * @param enchantLevel the level of the used enchantment
     * @param event        the event details (BlockBreakEvent or BlockDamageEvent))
     */
    public void applyToolEffect(Player player, Block block, int enchantLevel, BlockEvent event) {

    }

    /**
     * Applies when the player left or right clicks
     *
     * @param player      the player with the enchantment
     * @param enchatLevel the level of the used enchantment
     * @param event       the event details
     */
    public void applyMiscEffect(Player player, int enchatLevel, PlayerInteractEvent event) {

    }

    /**
     * Applies the effects when the item is equipped
     *
     * @param player       the player that equipped it
     * @param enchantLevel the level of the used enchantment
     */
    public void applyEquipEffect(Player player, int enchantLevel) {

    }

    /**
     * Applies effects when the item is unequipped
     *
     * @param player       the player that unequipped it
     * @param enchantLevel the level of the used enchantment
     */
    public void applyUnequipEfect(Player player, int enchantLevel) {

    }
}
