package org.s1queence.plugin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.s1queence.plugin.CuffItems;
import org.s1queence.plugin.items.CuffItem;
import org.s1queence.plugin.classes.CuffManager;
import org.s1queence.plugin.classes.UnCuffProcess;
import org.s1queence.plugin.items.*;

import java.util.List;

import static org.s1queence.api.S1TextUtils.getConvertedTextFromConfig;
import static org.s1queence.api.countdown.CountDownAction.isPlayerInCountDownAction;

public class UnCuffPlayerListener extends PlayerInteractEntityListener implements Listener {

    public UnCuffPlayerListener(CuffItems plugin) {
        super(plugin);
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (!e.getHand().equals(EquipmentSlot.HAND)) return;
        Entity entity = e.getRightClicked();
        if (!(entity instanceof Player)) return;
        Player player = e.getPlayer();
        Player target = (Player) entity;
        ItemStack item = player.getInventory().getItemInMainHand();


        if (cm.isHandsBlocked(player) || isPlayerInCountDownAction(player) || isPlayerInCountDownAction(target) || cm.isCuffItem(item)) return;

        List<CuffItem> playerCuffItems = cm.getPlayerCuffItems(target);
        if (playerCuffItems.isEmpty()) return;


        int seconds = 0;
        UnCuffProcess.UnCuffActionType ucat = null;
        CuffManager.CuffItemType cit = null;

        for (CuffItem ci : playerCuffItems) {
            cit = ci.getCuffItemType();

            if (cit.equals(CuffManager.CuffItemType.HANDCUFFS)) {
                Handcuffs cuffs = (Handcuffs) cm.getByType(cit);
                if (cuffs.getKey().equals(item)) {
                    seconds = cuffs.getKeyTimeToUnCuff();
                    ucat = UnCuffProcess.UnCuffActionType.DEFAULT;
                    break;
                }

                if (cm.isTool(item)) {
                    seconds = cuffs.getTimeToBreak();
                    ucat = UnCuffProcess.UnCuffActionType.BREAK;
                    break;
                }

                continue;
            }

            if (cit.equals(CuffManager.CuffItemType.SHACKLES)) {
                Shackles cuffs = (Shackles) cm.getByType(cit);
                if (cuffs.getKey().equals(item)) {
                    seconds = cuffs.getKeyTimeToUnCuff();
                    ucat = UnCuffProcess.UnCuffActionType.DEFAULT;
                    break;
                }

                if (cm.isTool(item)) {
                    seconds = cuffs.getTimeToBreak();
                    ucat = UnCuffProcess.UnCuffActionType.BREAK;
                    break;
                }

                continue;
            }

            if (cit.equals(CuffManager.CuffItemType.ROPE)) {
                Rope rope = (Rope) cm.getByType(cit);

                if (item.getType().equals(Material.SHEARS) || item.getType().toString().contains("SWORD")) {
                    seconds = rope.getTimeToCut();
                    ucat = UnCuffProcess.UnCuffActionType.BREAK;
                } else {
                    seconds = rope.getTimeToUnCuff();
                    ucat = UnCuffProcess.UnCuffActionType.DEFAULT;
                }

                break;
            }

            if (cit.equals(CuffManager.CuffItemType.PSYCHO_JACKET)) {
                PsychoJacket pj = (PsychoJacket) cm.getByType(cit);
                seconds = pj.getTimeToUnCuff();
                ucat = UnCuffProcess.UnCuffActionType.DEFAULT;
                break;
            }
        }

        if (ucat == null) return;

        new UnCuffProcess(
                player,
                target,
                seconds,
                false,
                true,
                plugin.getProgressBar(),
                cm,
                cit,
                ucat,
                plugin,
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.every_tick.action_bar_both", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.every_tick.player.title", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.every_tick.player.subtitle", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.every_tick.target.title", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.every_tick.target.subtitle", pName),

                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.complete.action_bar_both", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.complete.player.title", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.complete.player.subtitle", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.complete.target.title", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.complete.target.subtitle", pName),

                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.cancel.action_bar_both", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.cancel.player.title", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.cancel.player.subtitle", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.cancel.target.title", pName),
                getConvertedTextFromConfig(textConfig, "unCuff_process_messages.cancel.target.subtitle", pName)
        );
    }
}
