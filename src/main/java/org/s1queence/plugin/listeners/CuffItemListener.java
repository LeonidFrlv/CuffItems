package org.s1queence.plugin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.s1queence.plugin.classes.CuffManager;

public class CuffItemListener implements Listener {
    private final CuffManager cm;
    public CuffItemListener(CuffManager cm) {this.cm = cm;}

    @EventHandler
    private void onPlayerClick(InventoryClickEvent e) {
        if (cm.hasCuffItemsImmune((Player) e.getWhoClicked())) return;
        Inventory inv = e.getClickedInventory();
        if (inv == null) return;
        InventoryAction action = e.getAction();
        ItemStack item = e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) ? e.getCursor() : e.getCurrentItem();
        if (!cm.isCuffItem(item)) return;
        if (inv.getType().equals(InventoryType.PLAYER) && action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) e.setCancelled(true);
        if (e.getSlotType().equals(InventoryType.SlotType.ARMOR)) e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == null) return;
        if (!cm.isCuffItem(e.getItem())) return;
        if (e.getClickedBlock() != null && e.getClickedBlock().getType().isInteractable()) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void onPlayerToggleSprint(PlayerToggleSprintEvent e) {
        if (!e.isSprinting()) return;
        Player player = e.getPlayer();
        if (cm.hasCuffItemsImmune(player)) return;
        if (!cm.hasShackles(player)) return;
        int foodLevel = player.getFoodLevel();
        player.setFoodLevel(1);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setFoodLevel(foodLevel);
                player.setSprinting(false);
                e.setCancelled(true);
                cancel();
            }
        }.runTaskTimer(cm.getPlugin(), 5, 1);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        ItemStack cursor = e.getCursor();
        ItemStack clicked = e.getCurrentItem();
        if (cm.getHandsBlocker().equals(clicked)) e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        if (cm.hasCuffItemsImmune(player)) return;
        if (!cm.isCuffItem(clicked) && !cm.isCuffItem(cursor)) return;
        InventoryHolder holder = e.getClickedInventory().getHolder();
        if (!(holder instanceof Player) || player.equals(holder)) return;
        e.setCancelled(true);
    }
}
