package org.s1queence.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.s1queence.api.countdown.progressbar.ProgressBar;
import org.s1queence.plugin.classes.CuffManager;
import org.s1queence.plugin.items.CuffItem;
import org.s1queence.plugin.items.Handcuffs;
import org.s1queence.plugin.items.Shackles;
import org.s1queence.plugin.libs.YamlDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.s1queence.api.S1TextUtils.consoleLog;
import static org.s1queence.api.S1TextUtils.getConvertedTextFromConfig;
import static org.s1queence.api.S1Utils.sendActionBarMsg;

public class CuffItemsCommand implements CommandExecutor {
    private final CuffItems plugin;
    public CuffItemsCommand(CuffItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        int length = args.length;
        if (length == 0) return false;
        CuffItemsCommandAction cica = CuffItemsCommandAction.fromString(args[0]);
        if (cica == null) return false;

        YamlDocument textConfig = plugin.getTextConfig();
        String pName = plugin.getName();
        CuffManager cm = plugin.getCm();
        boolean isSenderPlayer = sender instanceof Player;
        Map<CuffManager.CuffItemType, CuffItem> cuffItemsMap = cm.getCuffItemsMap();

        switch (cica) {
            case RELOAD: {
                YamlDocument optionsConfig = plugin.getOptionsConfig();
                YamlDocument itemsConfig = plugin.getItemsConfig();

                try {
                    File df = plugin.getDataFolder();
                    File textConfigFile = new File(df, "text.yml");
                    File itemsConfigFile = new File(df, "items.yml");
                    File optionsConfigFile = new File(df, "options.yml");
                    plugin.setTextConfig(textConfigFile.exists() ? YamlDocument.create(textConfigFile) : YamlDocument.create(new File(df, "text.yml"), Objects.requireNonNull(plugin.getResource("text.yml"))));
                    plugin.setItemsConfig(itemsConfigFile.exists() ? YamlDocument.create(itemsConfigFile) : YamlDocument.create(new File(df, "items.yml"), Objects.requireNonNull(plugin.getResource("items.yml"))));
                    plugin.setOptionsConfig(optionsConfigFile.exists() ? YamlDocument.create(optionsConfigFile) : YamlDocument.create(new File(df, "options.yml"), Objects.requireNonNull(plugin.getResource("options.yml"))));

                    if (plugin.getItemsConfig().hasDefaults()) Objects.requireNonNull(plugin.getItemsConfig().getDefaults()).clear();

                    textConfig.reload();
                    itemsConfig.reload();
                    optionsConfig.reload();
                } catch (IOException ignored) {

                }

                plugin.setProgressBar(new ProgressBar(
                        0,
                        1,
                        optionsConfig.getInt("progress_bar.max_bars"),
                        optionsConfig.getString("progress_bar.symbol"),
                        ChatColor.translateAlternateColorCodes('&', optionsConfig.getString("progress_bar.border_left")),
                        ChatColor.translateAlternateColorCodes('&', optionsConfig.getString("progress_bar.border_right")),
                        ChatColor.getByChar(optionsConfig.getString("progress_bar.color")),
                        ChatColor.getByChar(optionsConfig.getString("progress_bar.complete_color")),
                        ChatColor.getByChar(optionsConfig.getString("progress_bar.percent_color"))
                ));

                plugin.setCuffManager(new CuffManager(plugin));

                String reloadMsg = getConvertedTextFromConfig(textConfig, "onReload_msg", pName);
                if (isSenderPlayer) sender.sendMessage(reloadMsg);
                consoleLog(reloadMsg, plugin);

                break;
            }

            case GET: {
                if (length != 1) return false;
                if (!isSenderPlayer) return true;
                Player player = (Player) sender;
                Inventory inv = Bukkit.createInventory((player), 27);

                for (CuffManager.CuffItemType cit : cuffItemsMap.keySet()) {
                    CuffItem ci = cuffItemsMap.get(cit);
                    inv.addItem(ci.getItem());
                    if (cit.equals(CuffManager.CuffItemType.HANDCUFFS)) inv.addItem(((Handcuffs) ci).getKey());
                    if (cit.equals(CuffManager.CuffItemType.SHACKLES)) inv.addItem(((Shackles)ci).getKey());
                }

                player.openInventory(inv);
                break;
            }

            case CUFF:
            case UNCUFF: {
                if (length < 2 || length > 3) return false;
                if (!isSenderPlayer) return true;
                Player player = (Player) sender;
                Player target = plugin.getServer().getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(getConvertedTextFromConfig(textConfig, "player_not_found", pName));
                    return true;
                }

                boolean isActionCuff = cica.equals(CuffItemsCommandAction.CUFF);
                boolean isActionUnCuff = cica.equals(CuffItemsCommandAction.UNCUFF);

                if (length != 3 && isActionCuff) return false;

                List<CuffItem> targetCuffItems = cm.getPlayerCuffItems(target);

                if (isActionUnCuff && cm.isHandsBlocked(target)) cm.removeHandsBlocker(target);

                if (targetCuffItems.isEmpty() && isActionUnCuff) {
                    player.sendMessage(getConvertedTextFromConfig(textConfig, "not_cuffed", pName));
                    return true;
                }

                if (length == 3) {
                    CuffManager.CuffItemType cit = CuffManager.CuffItemType.fromString(args[2]);

                    if (cit == null) {
                        player.sendMessage(getConvertedTextFromConfig(textConfig, "unknown_item", pName));
                        return true;
                    }

                    CuffItem ci = cm.getByType(cit);

                    if (isActionCuff) {
                        cm.setCuffItem(ci.getItem(), target);
                        if (!ci.getCuffItemType().equals(CuffManager.CuffItemType.SHACKLES)) cm.blockPlayerHands(target);
                    }

                    if (isActionUnCuff) cm.removeCuffItem(ci, target, true);


                } else {
                    for (CuffItem ci : targetCuffItems)
                        cm.removeCuffItem(ci, target, true);
                }

                String path = isActionCuff ? "cuff_process_messages" : "unCuff_process_messages";

                target.sendTitle(getConvertedTextFromConfig(textConfig, path + ".complete.target.title", pName), getConvertedTextFromConfig(textConfig, path + ".complete.target.subtitle", pName), 0, 75, 15);
                sendActionBarMsg(target, getConvertedTextFromConfig(textConfig, path + ".complete.action_bar_both", pName));
                player.sendTitle(getConvertedTextFromConfig(textConfig, path + ".complete.player.title", pName), getConvertedTextFromConfig(textConfig, path + ".complete.player.subtitle", pName), 0, 75, 15);
                sendActionBarMsg(player, getConvertedTextFromConfig(textConfig, path + ".complete.action_bar_both", pName));
                break;
            }
        }

        return true;
    }

    public enum CuffItemsCommandAction {
        RELOAD ("reload"),
        UNCUFF ("unCuff"),
        CUFF ("cuff"),
        GET ("get");

        private final String value;
        CuffItemsCommandAction(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static CuffItemsCommandAction fromString(String str) {
            for (CuffItemsCommandAction stringValue : CuffItemsCommandAction.values())
                if (stringValue.toString().equalsIgnoreCase(str)) return stringValue;

            return null;
        }

    }
}
