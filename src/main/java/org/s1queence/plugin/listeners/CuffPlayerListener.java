package org.s1queence.plugin.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.s1queence.plugin.CuffItems;
import org.s1queence.plugin.classes.CuffProcess;
import org.s1queence.plugin.items.CuffItem;

import java.util.List;

import static org.s1queence.api.S1TextUtils.getConvertedTextFromConfig;
import static org.s1queence.api.countdown.CountDownAction.isPlayerInCountDownAction;

public class CuffPlayerListener extends PlayerInteractEntityListener implements Listener {

    public CuffPlayerListener(CuffItems plugin) {
        super(plugin);
    }

    private boolean hasSameCuffItem(Player target, CuffItem ci) {
        List<CuffItem> targetCuffItems = cm.getPlayerCuffItems(target);
        if (targetCuffItems.isEmpty()) return false;

        for (CuffItem currentCi : targetCuffItems)
            if (currentCi.getCuffItemType().equals(ci.getCuffItemType()) || cm.getArmorType(currentCi.getItem()).equals(cm.getArmorType(ci.getItem()))) return true;

        return false;
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (!e.getHand().equals(EquipmentSlot.HAND)) return;
        Entity entity = e.getRightClicked();
        if (!(entity instanceof Player)) return;
        Player player = e.getPlayer();
        Player target = (Player) entity;
        if (cm.isHandsBlocked(player) || isPlayerInCountDownAction(player) || isPlayerInCountDownAction(target)) return;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!cm.isCuffItem(item)) return;

        CuffItem ci = cm.getCuffItem(item);
        if (hasSameCuffItem(target, ci)) return;

        int seconds = ci.getTimeToCuff();

        new CuffProcess(
                player,
                target,
                seconds,
                false,
                true,
                plugin.getProgressBar(),
                cm,
                cm.getCuffItemType(item),
                plugin,
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.every_tick.action_bar_both", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.every_tick.player.title", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.every_tick.player.subtitle", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.every_tick.target.title", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.every_tick.target.subtitle", pName),

                getConvertedTextFromConfig(textConfig,"cuff_process_messages.complete.action_bar_both", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.complete.player.title", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.complete.player.subtitle", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.complete.target.title", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.complete.target.subtitle", pName),

                getConvertedTextFromConfig(textConfig,"cuff_process_messages.cancel.action_bar_both", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.cancel.player.title", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.cancel.player.subtitle", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.cancel.target.title", pName),
                getConvertedTextFromConfig(textConfig,"cuff_process_messages.cancel.target.subtitle", pName)
        );
    }
}
