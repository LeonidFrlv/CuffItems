package org.s1queence.plugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.s1queence.api.countdown.progressbar.ProgressBar;
import org.s1queence.plugin.classes.CuffManager;
import org.s1queence.plugin.libs.YamlDocument;
import org.s1queence.plugin.listeners.BlockedPlayerHandsListener;
import org.s1queence.plugin.listeners.CuffItemListener;
import org.s1queence.plugin.listeners.CuffPlayerListener;
import org.s1queence.plugin.listeners.UnCuffPlayerListener;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.s1queence.api.S1TextUtils.*;

public final class CuffItems extends JavaPlugin implements Listener {
    private YamlDocument textConfig;
    private YamlDocument itemsConfig;
    private YamlDocument optionsConfig;
    private CuffManager cm;
    private ProgressBar pb;

    @Override
    public void onEnable() {
        try {
            File textConfigFile = new File(getDataFolder(), "text.yml");
            File itemsConfigFile = new File(getDataFolder(), "items.yml");
            File optionsConfigFile = new File(getDataFolder(), "options.yml");
            textConfig = textConfigFile.exists() ? YamlDocument.create(textConfigFile) : YamlDocument.create(new File(getDataFolder(), "text.yml"), Objects.requireNonNull(getResource("text.yml")));
            itemsConfig = itemsConfigFile.exists() ? YamlDocument.create(itemsConfigFile) : YamlDocument.create(new File(getDataFolder(), "items.yml"), Objects.requireNonNull(getResource("items.yml")));
            optionsConfig = optionsConfigFile.exists() ? YamlDocument.create(optionsConfigFile) : YamlDocument.create(new File(getDataFolder(), "options.yml"), Objects.requireNonNull(getResource("options.yml")));
        } catch (IOException ignored) {

        }

        pb = new ProgressBar(
                0,
                1,
                optionsConfig.getInt("progress_bar.max_bars"),
                optionsConfig.getString("progress_bar.symbol"),
                ChatColor.translateAlternateColorCodes('&', optionsConfig.getString("progress_bar.border_left")),
                ChatColor.translateAlternateColorCodes('&', optionsConfig.getString("progress_bar.border_right")),
                ChatColor.getByChar(optionsConfig.getString("progress_bar.color")),
                ChatColor.getByChar(optionsConfig.getString("progress_bar.complete_color")),
                ChatColor.getByChar(optionsConfig.getString("progress_bar.percent_color"))
        );

        cm = new CuffManager(this);

        consoleLog(getConvertedTextFromConfig(textConfig, "onEnable_msg", getName()), this);

        getServer().getPluginManager().registerEvents(new BlockedPlayerHandsListener(cm), this);
        getServer().getPluginManager().registerEvents(new CuffItemListener(cm), this);
        getServer().getPluginManager().registerEvents(new CuffPlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new UnCuffPlayerListener(this), this);
        Objects.requireNonNull(getServer().getPluginCommand("cuffItems")).setExecutor(new CuffItemsCommand(this));
    }

    @Override
    public void onDisable() {
        consoleLog(getConvertedTextFromConfig(textConfig, "onDisable_msg", getName()), this);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        if (optionsConfig.getBoolean("set_resource_pack")) e.getPlayer().setResourcePack(optionsConfig.getString("resource_pack"));
    }

    public CuffManager getCm() {
        return cm;
    }

    public ProgressBar getProgressBar() {
        return pb;
    }

    public YamlDocument getItemsConfig() {
        return itemsConfig;
    }

    public void setItemsConfig(YamlDocument newState) {itemsConfig = newState;}

    public YamlDocument getOptionsConfig() {
        return optionsConfig;
    }

    public void setProgressBar(ProgressBar pb) {
        this.pb = pb;
    }

    public void setCuffManager(CuffManager cm) {
        this.cm = cm;
    }

    public void setOptionsConfig(YamlDocument newState) {optionsConfig = newState;}

    public YamlDocument getTextConfig() {
        return textConfig;
    }

    public void setTextConfig(YamlDocument newState) {textConfig = newState;}
}
