/**
 * *****************************************************************************
 * EnchantAPI
 *
 * EEquip
 *
 * @author SuitedSlime
 * @licence Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * *****************************************************************************
 */

package com.suitedslime.enchantapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Hashtable;

public class EEquip extends BukkitRunnable {

    // Player reference
    Player player;

    /**
     * Table of player data
     */
    static Hashtable<String, ItemStack[]> equipment = new Hashtable<String, ItemStack[]>();

    /**
     * Loads the equipment of the given player
     *
     * @param player player to load
     */
    static void loadPlayer(Player player) {
        equipment.put(player.getName(), player.getEquipment().getArmorContents());
    }

    /**
     * Clears the data for the given player
     *
     * @param player player to clear
     */
    static void clearPlayer(Player player) {
        equipment.remove(player.getName());
    }

    /**
     * Clears all player data
     */
    static void clear() {
        equipment.clear();
    }

    /**
     * Constructor
     *
     * @param player player to re-evaluate
     */
    public EEquip(Player player) {
        this.player = player;
    }

    /**
     * Performs checks for changes to player equipemtn
     */
    public void run() {
        ItemStack[] equips = this.player.getEquipment().getArmorContents();
        ItemStack[] previous = (ItemStack[]) equipment.get(this.player.getName());
        for (int i = 0; i < equips.length; i++) {
            if (!equips[i].toString().equalsIgnoreCase(previous[i].toString())) {
                doEquip(equips[i]);
                doUnequip(previous[i]);
            }
        }
        equipment.put(this.player.getName(), equips);
    }

    /**
     * Applies the equip actions to the given item
     *
     * @param item the equipment that was just equipped
     */
    private void doEquip(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasLore()) return;
        for (String lore : meta.getLore()) {
            String name = ENameParser.parseName(lore);
            int level = ENameParser.parseLevel(lore);
            if (name == null) continue;
            if (level == 0) continue;
            if (EnchantAPI.isRegistered(name)) {
                EnchantAPI.getEnchantment(name).applyEquipEffect(player, level);
            }
        }
    }

    /**
     * Applies unequip actions to the given item
     *
     * @param item the equipment that was just unequipped
     */
    private void doUnequip(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasLore()) return;
        for (String lore : meta.getLore()) {
            String name = ENameParser.parseName(lore);
            int level = ENameParser.parseLevel(lore);
            if (name == null) continue;
            if (level == 0) continue;
            if (EnchantAPI.isRegistered(name)) {
                EnchantAPI.getEnchantment(name).applyUnequipEfect(player, level);
            }
        }
    }
}
