package org.s1queence.plugin.items;

import org.s1queence.plugin.classes.CuffManager;
import org.s1queence.plugin.libs.YamlDocument;

public class PsychoJacket extends CuffItem {
    private final int timeToUnCuff;

    public PsychoJacket(YamlDocument config, CuffManager cm) {
        super(config, "psycho_jacket", cm, CuffManager.CuffItemType.PSYCHO_JACKET);
        this.timeToUnCuff = config.getInt("psycho_jacket.time_to_unCuff");
    }

    public int getTimeToUnCuff() {
        return timeToUnCuff;
    }
}
