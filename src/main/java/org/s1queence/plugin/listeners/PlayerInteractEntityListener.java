package org.s1queence.plugin.listeners;

import org.s1queence.plugin.CuffItems;
import org.s1queence.plugin.classes.CuffManager;
import org.s1queence.plugin.libs.YamlDocument;

public abstract class PlayerInteractEntityListener {
    protected YamlDocument textConfig;
    protected CuffManager cm;
    protected CuffItems plugin;
    protected String pName;

    public PlayerInteractEntityListener(CuffItems plugin) {
        this.plugin = plugin;
        this.textConfig = plugin.getTextConfig();
        this.cm = plugin.getCm();
        this.pName = plugin.getName();
    }


}
