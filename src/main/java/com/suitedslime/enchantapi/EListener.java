/**
 * *****************************************************************************
 * EnchantAPI
 *
 * EListener
 *
 * @author SuitedSlime
 * @licence Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * *****************************************************************************
 */

package com.suitedslime.enchantapi;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EListener implements Listener {

    Plugin plugin;

    public EListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (!(event.getDamager() instanceof LivingEntity)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity damager = (LivingEntity) event.getDamager();
        for (Map.Entry entry : getValidEnchantments(getItems(damager)).entrySet())
            ((CustomEnchantment) entry.getKey()).applyEffect(damager, (LivingEntity) event.getEntity(),
                    ((Integer) entry.getValue()).intValue(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity damaged = (LivingEntity) event.getEntity();
        LivingEntity damager = (event.getDamager() instanceof Projectile) ? ((Projectile) event.getDamager())
                .getShooter() : (event.getDamager() instanceof LivingEntity) ? (LivingEntity) event.getDamager() : null;

        for (Map.Entry entry : getValidEnchantments(getItems(damaged)).entrySet())
            ((CustomEnchantment) entry.getKey()).applyDefenseEffect(damaged, damager,
                    ((Integer) entry.getValue()).intValue(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity damaged = (LivingEntity) event.getEntity();
        for (Map.Entry entry : getValidEnchantments(getItems(damaged)).entrySet())
            ((CustomEnchantment) entry.getKey()).applyDefenseEffect(damaged, null,
                    ((Integer) entry.getValue()).intValue(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamaged(EntityDamageByBlockEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity damaged = (LivingEntity) event.getEntity();
        for (Map.Entry entry : getValidEnchantments(getItems(damaged)).entrySet())
            ((CustomEnchantment) entry.getKey()).applyDefenseEffect(damaged, null,
                    ((Integer) entry.getValue()).intValue(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageBlock(BlockDamageEvent event) {
        for (Map.Entry entry : getValidEnchantments(getItems(event.getPlayer())).entrySet())
            ((CustomEnchantment) entry.getKey()).applyToolEffect(event.getPlayer(), event.getBlock(),
                    ((Integer) entry.getValue()).intValue(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        for (Map.Entry entry : getValidEnchantments(getItems(event.getPlayer())).entrySet())
            ((CustomEnchantment) entry.getKey()).applyToolEffect(event.getPlayer(), event.getBlock(),
                    ((Integer) entry.getValue()).intValue(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        for (Map.Entry entry : getValidEnchantments(getItems(event.getPlayer())).entrySet()) {
            ((CustomEnchantment) entry.getKey()).applyMiscEffect(event.getPlayer(),
                    ((Integer) entry.getValue()).intValue(), event);
        }

        new EEquip(event.getPlayer()).runTaskLater(this.plugin, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEquip(InventoryClickEvent event) {
        new EEquip(this.plugin.getServer().getPlayer(event.getWhoClicked().getName())).runTaskLater(this.plugin, 1L);
    }

    @EventHandler
    public void onBreak(PlayerItemBreakEvent event) {
        new EEquip(event.getPlayer()).runTaskLater(this.plugin, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConnect(PlayerJoinEvent event) {
        EEquip.loadPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDisconnect(PlayerQuitEvent event) {
        EEquip.clearPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        for (CustomEnchantment enchantment : EnchantAPI.getEnchantments())
            if (enchantment.canEnchantOnto(item)) {
                int enchantLevel = enchantment.getEnchantmentLevel(event.getExpLevelCost());
                if (enchantLevel > 0) enchantment.addToItem(item, enchantLevel);
            }
    }

    private Map<CustomEnchantment, Integer> getValidEnchantments(ArrayList<ItemStack> items) {
        Map validEnchantments = new HashMap();
        for (ItemStack item : items) {
            ItemMeta meta = item.getItemMeta();
            if ((meta != null) && (meta.hasLore())) {
                for (String lore : meta.getLore()) {
                    String name = ENameParser.parseName(lore);
                    int level = ENameParser.parseLevel(lore);
                    if ((name != null) && (level != 0)) {
                        if (EnchantAPI.isRegistered(name)) {
                            validEnchantments.put(EnchantAPI.getEnchantment(name), Integer.valueOf(level));
                        }
                    }
                }
            }
        }
        return validEnchantments;
    }

    private ArrayList<ItemStack> getItems(LivingEntity entity) {
        ItemStack[] armor = entity.getEquipment().getArmorContents();
        ItemStack weapon = entity.getEquipment().getItemInHand();
        ArrayList items = new ArrayList(Arrays.asList(armor));
        items.add(weapon);
        return items;
    }

}
