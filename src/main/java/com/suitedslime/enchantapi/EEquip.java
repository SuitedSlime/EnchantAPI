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

    static Hashtable<String, ItemStack[]> equipment = new Hashtable();
    Player player;

    static void loadPlayer(Player player) {
        equipment.put(player.getName(), player.getEquipment().getArmorContents());
    }

    static void clearPlayer(Player player) {
        equipment.remove(player.getName());
    }

    static void clear() {
        equipment.clear();
    }

    public EEquip(Player player) {
        this.player = player;
    }

    public void run() {
        ItemStack[] equips = this.player.getEquipment().getArmorContents();
        ItemStack[] previous = (ItemStack[])equipment.get(this.player.getName());
        for (int i = 0; i <equips.length; i++) {
            if (!equips[i].toString().equalsIgnoreCase(previous[i].toString())) {
                doEquip(equips[i]);
                doUnequip(previous[i]);
            }
        }
        equipment.put(this.player.getName(), equips);
    }

    private void doEquip(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasLore()) return;
        for (String lore : meta.getLore()) {
            String name = ENameParser.parseName(lore);
            int level = ENameParser.parseLevel(lore);
            if ((name != null) && (level != 0)) {
                if (EnchantAPI.isRegistered(name)) {
                    EnchantAPI.getEnchantment(name).applyEquipEffect(this.player, level);
                }
            }
        }
    }

    private void doUnequip(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasLore()) return;
        for (String lore : meta.getLore()) {
            String name = ENameParser.parseName(lore);
            int level = ENameParser.parseLevel(lore);
            if ((name != null) && (level != 0)) {
                if (EnchantAPI.isRegistered(name)) {
                    EnchantAPI.getEnchantment(name).applyUnequipEfect(this.player, level);
                }
            }
        }
    }
}
