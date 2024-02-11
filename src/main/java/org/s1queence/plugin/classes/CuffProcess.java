package org.s1queence.plugin.classes;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.s1queence.api.countdown.CountDownAction;
import org.s1queence.api.countdown.progressbar.ProgressBar;
import org.s1queence.plugin.CuffItems;

import static org.s1queence.api.S1Utils.removeItemFromPlayerInventory;

public class CuffProcess extends CountDownAction {

    private final CuffManager.CuffItemType cit;

    public CuffProcess(
            @NotNull Player player,
            @NotNull Player target,
            int seconds,
            boolean isDoubleRunnableAction,
            boolean isClosePlayersInventoriesEveryTick,
            @NotNull ProgressBar pb,
            @NotNull CuffManager cm,
            @NotNull CuffManager.CuffItemType cit,
            @NotNull CuffItems plugin,
            @NotNull String everyTickBothActionBarMsg,
            @NotNull String everyTickPlayerTitle,
            @NotNull String everyTickPlayerSubtitle,
            @Nullable String everyTickTargetTitle,
            @Nullable String everyTickTargetSubtitle,
            @NotNull String completeBothActionBarMsg,
            @NotNull String completePlayerTitle,
            @NotNull String completePlayerSubtitle,
            @Nullable String completeTargetTitle,
            @Nullable String completeTargetSubtitle,
            @NotNull String cancelBothActionBarMsg,
            @NotNull String cancelPlayerTitle,
            @NotNull String cancelPlayerSubtitle,
            @Nullable String cancelTargetTitle,
            @Nullable String cancelTargetSubtitle)
    {
        super(player, target, seconds, isDoubleRunnableAction, isClosePlayersInventoriesEveryTick, pb, plugin, everyTickBothActionBarMsg, everyTickPlayerTitle, everyTickPlayerSubtitle, everyTickTargetTitle, everyTickTargetSubtitle, completeBothActionBarMsg, completePlayerTitle, completePlayerSubtitle, completeTargetTitle, completeTargetSubtitle, cancelBothActionBarMsg, cancelPlayerTitle, cancelPlayerSubtitle, cancelTargetTitle, cancelTargetSubtitle);
        this.cit = cit;
        actionCountDown();

        World world = player.getWorld();
        Location loc = player.getLocation();
        ItemStack li = getLaunchItem();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isActionCanceled()) {
                    cancel();
                    return;
                }

                if (isPreprocessActionComplete()) {
                    removeItemFromPlayerInventory(player, li);
                    cm.setCuffItem(li, target);
                    if (!cit.equals(CuffManager.CuffItemType.SHACKLES)) cm.blockPlayerHands(target);
                    world.playSound(loc, getSound(), 2.0f, 1.0f);
                    cancelAction(false);
                    cancel();
                }

            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private Sound getSound() {
        if (cit.equals(CuffManager.CuffItemType.ROPE)) return Sound.ENTITY_LEASH_KNOT_PLACE;
        if (cit.equals(CuffManager.CuffItemType.PSYCHO_JACKET)) return Sound.ITEM_ARMOR_EQUIP_LEATHER;
        return Sound.BLOCK_CHAIN_PLACE;
    }
}
