package org.s1queence.plugin.items;

import org.bukkit.inventory.ItemStack;
import org.s1queence.plugin.classes.CuffManager;
import org.s1queence.plugin.libs.YamlDocument;

import static org.s1queence.api.S1TextUtils.createItemFromMap;

public class Shackles extends CuffItem {
    private final int keyTimeToUnCuff;
    private final int timeToBreak;
    private final ItemStack key;
    public Shackles(YamlDocument config, CuffManager cm) {
        super(config, "shackles", cm, CuffManager.CuffItemType.SHACKLES);
        key = createItemFromMap(config.getSection("shackles.key_item").getStringRouteMappedValues(true));
        keyTimeToUnCuff= config.getInt("shackles.key_time_to_unCuff");
        timeToBreak = config.getInt("shackles.time_to_break");
    }

    public ItemStack getKey() {
        return key;
    }

    public int getKeyTimeToUnCuff() {
        return keyTimeToUnCuff;
    }

    public int getTimeToBreak() {
        return timeToBreak;
    }
}
