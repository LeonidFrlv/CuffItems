package org.s1queence.plugin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.s1queence.plugin.classes.CuffManager;

public class BlockedPlayerHandsListener implements Listener {
    private final CuffManager cm;
    public BlockedPlayerHandsListener(CuffManager cm) {this.cm = cm;}
    @EventHandler
    private void onPlayerItemHeld(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        ItemStack prevItem = player.getInventory().getItem(e.getPreviousSlot());
        if (prevItem != null && prevItem.equals(cm.getHandsBlocker())) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerSwapHands(PlayerSwapHandItemsEvent e) {
        if (cm.isHandsBlocked(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    private void onEntityPickupItem(EntityPickupItemEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) return;
        if (cm.isHandsBlocked((Player) entity)) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
        if (cm.isHandsBlocked(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerOpenInventory(InventoryOpenEvent e) {
        if (cm.isHandsBlocked((Player) e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerPlaceBlock(BlockPlaceEvent e) {
        if (cm.isHandsBlocked(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerBreakBlock(BlockBreakEvent e) {
        if (cm.isHandsBlocked(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == null) return;
        if (cm.isHandsBlocked(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent e) {
        for (ItemStack is : e.getDrops()) {
            if (is != null && is.equals(cm.getHandsBlocker())) is.setType(Material.AIR);
        }
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (entity instanceof Vehicle) return;
        if (cm.isHandsBlocked(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    private void onEntityDamageDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getDamager();
        if (!(entity instanceof Player)) return;
        if (cm.isHandsBlocked((Player) entity)) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerConsume(PlayerItemConsumeEvent e) {
        if (cm.isHandsBlocked(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        if (cm.isHandsBlocked((Player) e.getWhoClicked())) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent e) {
        if (cm.isHandsBlocked(e.getPlayer()) || cm.getHandsBlocker().equals(e.getItemDrop().getItemStack())) e.setCancelled(true);
    }
}
