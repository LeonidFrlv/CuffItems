package org.s1queence.plugin.items;

import org.bukkit.inventory.ItemStack;
import org.s1queence.plugin.classes.CuffManager;
import org.s1queence.plugin.libs.YamlDocument;

import static org.s1queence.api.S1TextUtils.createItemFromMap;

public class Handcuffs extends CuffItem {
    private final int keyTimeToUnCuff;
    private final int timeToBreak;
    private final ItemStack key;
    public Handcuffs(YamlDocument config, CuffManager cm) {
        super(config, "handcuffs", cm, CuffManager.CuffItemType.HANDCUFFS);
        key = createItemFromMap(config.getSection("handcuffs.key_item").getStringRouteMappedValues(true));
        keyTimeToUnCuff= config.getInt("handcuffs.key_time_to_unCuff");
        timeToBreak = config.getInt("handcuffs.time_to_break");
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
