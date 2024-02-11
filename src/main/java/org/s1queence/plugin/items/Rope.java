package org.s1queence.plugin.items;

import org.s1queence.plugin.classes.CuffManager;
import org.s1queence.plugin.libs.YamlDocument;

public class Rope extends CuffItem {
    private final int timeToUnCuff;
    private final int timeToCut;
    public Rope(YamlDocument config, CuffManager cm) {
        super(config, "rope", cm, CuffManager.CuffItemType.ROPE);
        this.timeToUnCuff = config.getInt("rope.time_to_unCuff");
        this.timeToCut = config.getInt("rope.time_to_cut");
    }

    public int getTimeToUnCuff() {
        return timeToUnCuff;
    }

    public int getTimeToCut() {
        return timeToCut;
    }
}
