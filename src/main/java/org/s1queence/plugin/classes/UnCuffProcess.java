package org.s1queence.plugin.classes;

import org.bukkit.Location;
import org.bukkit.Particle;
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
import org.s1queence.plugin.items.CuffItem;

import static org.s1queence.api.S1Utils.setItemDamage;

public class UnCuffProcess extends CountDownAction {
    private final CuffManager.CuffItemType cit;
    private final UnCuffActionType ucat;

    public UnCuffProcess(
                        @NotNull Player player,
                        @NotNull Player target,
                        int seconds,
                        boolean isDoubleRunnableAction,
                        boolean isClosePlayersInventoriesEveryTick,
                        @NotNull ProgressBar pb,
                        @NotNull CuffManager cm,
                        @NotNull CuffManager.CuffItemType cit,
                        @NotNull UnCuffActionType ucat,
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
        this.ucat = ucat;
        actionCountDown();

        CuffItem ci = cm.getByType(cit);
        World world = player.getWorld();
        Location loc = target.getLocation();
        ItemStack li = getLaunchItem();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isActionCanceled()) {
                    cancel();
                    return;
                }

                if (isPreprocessActionComplete()) {
                    Sound sound = getSound();
                    if (sound != null) world.playSound(loc, sound, 1.0f, 1.0f);

                    if (ucat.equals(UnCuffActionType.BREAK)) {
                        setItemDamage(li, player, 1);
                        cm.removeCuffItem(ci, target, false);
                        world.spawnParticle(Particle.ITEM_CRACK, loc, 10, 0.3, 0.5, 0.3, 0.0, ci.getItem());
                    }

                    if (ucat.equals(UnCuffActionType.DEFAULT)) cm.removeCuffItem(ci, target, true);

                    if (!cit.equals(CuffManager.CuffItemType.SHACKLES)) cm.removeHandsBlocker(target);

                    cancelAction(false);
                    cancel();
                }

            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private Sound getSound() {
        switch (cit) {
            case PSYCHO_JACKET: {
                return Sound.ENTITY_LEASH_KNOT_BREAK;
            }

            case ROPE: {
                if (ucat.equals(UnCuffActionType.BREAK)) return Sound.ENTITY_SHEEP_SHEAR;
                return Sound.ENTITY_LEASH_KNOT_BREAK;
            }

            case HANDCUFFS:
            case SHACKLES: {
                if (ucat.equals(UnCuffActionType.BREAK)) return Sound.BLOCK_CHAIN_BREAK;
                return Sound.BLOCK_CHAIN_FALL;
            }
        }

        return null;
    }

    public enum UnCuffActionType {
        BREAK,
        DEFAULT
    }

}
