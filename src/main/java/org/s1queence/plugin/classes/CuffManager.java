package org.s1queence.plugin.classes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.s1queence.plugin.CuffItems;
import org.s1queence.plugin.items.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.s1queence.api.S1TextUtils.createItemFromMap;

public class CuffManager {
    private final ItemStack handsBlocker;
    private final Handcuffs handcuffs;
    private final Rope rope;
    private final PsychoJacket psychoJacket;
    private final Shackles shackles;
    private final Map<CuffItemType, CuffItem> cuffItemsMap;
    private final CuffItems plugin;

    public CuffManager(CuffItems plugin) {
        this.plugin = plugin;
        handsBlocker = createItemFromMap(plugin.getItemsConfig().getSection("blocker_item").getStringRouteMappedValues(true));
        handcuffs = new Handcuffs(plugin.getItemsConfig(), this);
        rope = new Rope(plugin.getItemsConfig(), this);
        shackles = new Shackles(plugin.getItemsConfig(), this);
        psychoJacket = new PsychoJacket(plugin.getItemsConfig(), this);

        cuffItemsMap = new HashMap<>();
        cuffItemsMap.put(CuffItemType.HANDCUFFS, handcuffs);
        cuffItemsMap.put(CuffItemType.ROPE, rope);
        cuffItemsMap.put(CuffItemType.SHACKLES, shackles);
        cuffItemsMap.put(CuffItemType.PSYCHO_JACKET, psychoJacket);
    }

    public void setCuffItem(ItemStack item, Player target) {
        EquipmentSlot es = getArmorType(item);
        if (es == null) return;
        EntityEquipment equipment = target.getEquipment();
        if (equipment == null) return;
        ItemStack itemInSlot = equipment.getItem(es);
        if (!itemInSlot.getType().equals(Material.AIR)) target.getWorld().dropItemNaturally(target.getLocation(), itemInSlot);
        target.getEquipment().setItem(es, item);
    }

    public void removeCuffItem(CuffItem cuffItem, Player target, boolean dropCuffItem) {
        EquipmentSlot es = getArmorType(cuffItem.getItem());
        if (es == null) return;
        if (cuffItem.getItem().getType().equals(Material.AIR)) return;
        EntityEquipment equipment = target.getEquipment();
        if (equipment == null) return;
        if (dropCuffItem) target.getWorld().dropItemNaturally(target.getLocation(), equipment.getItem(es));
        target.getEquipment().setItem(es, null);
    }

    public List<CuffItem> getPlayerCuffItems(Player player) {
        List<CuffItem> cuffItems = new ArrayList<>();
        for (ItemStack current : player.getInventory().getArmorContents())
            if (isCuffItem(current)) cuffItems.add(getCuffItem(current));

        return cuffItems;
    }

    public boolean hasShackles(Player player) {
        for (CuffItem ci : getPlayerCuffItems(player))
            if (ci.equals(shackles.getItem())) return true;

        return false;
    }

    public Map<CuffItemType, CuffItem> getCuffItemsMap() {
        return cuffItemsMap;
    }

    public CuffItem getByType(CuffItemType cit) {
        return cuffItemsMap.get(cit);
    }

    public void blockPlayerHands(Player target) {
        if (hasCuffItemsImmune(target)) return;
        if (isHandsBlocked(target)) return;
        PlayerInventory inv = target.getInventory();
        inv.setHeldItemSlot(0);
        ItemStack itemInMainHand = inv.getItemInMainHand();
        ItemStack itemInOffHand = inv.getItemInOffHand();
        World world = target.getWorld();
        Location loc = target.getLocation();
        if (!itemInMainHand.getType().equals(Material.AIR)) world.dropItemNaturally(loc, itemInMainHand);
        if (!itemInOffHand.getType().equals(Material.AIR)) world.dropItemNaturally(loc, itemInOffHand);
        inv.setItem(inv.getHeldItemSlot(), handsBlocker);
        inv.setItem(40, handsBlocker);
    }

    public void removeHandsBlocker(Player target) {
        PlayerInventory inv = target.getInventory();
        inv.removeItem(handsBlocker);
        if (inv.getItemInOffHand().equals(handsBlocker)) inv.setItemInOffHand(null);
    }

    public EquipmentSlot getArmorType(ItemStack is) {
        if (is == null) return null;
        String type = is.getType().toString();
        return type.contains("HELMET") ? EquipmentSlot.HEAD :
                type.contains("CHESTPLATE") ? EquipmentSlot.CHEST :
                        type.contains("LEGGINGS") ? EquipmentSlot.LEGS :
                                type.contains("BOOTS") ? EquipmentSlot.FEET : null;
    }

    public ItemStack getHandsBlocker() {
        return handsBlocker;
    }

    public boolean isCuffItem(ItemStack item) {
        return handcuffs.equals(item) || rope.equals(item) || psychoJacket.equals(item) || shackles.equals(item);
    }

    public boolean isTool(ItemStack item) {
        String type = item.getType().toString();
        return type.contains("HOE") || type.contains("AXE") || type.contains("SHOVEL");
    }

    public CuffItemType getCuffItemType(ItemStack item) {
        for (CuffItemType cit : cuffItemsMap.keySet()) {
            CuffItem ci = cuffItemsMap.get(cit);
            if (ci.equals(item)) return cit;
        }

        return null;
    }

    public CuffItem getCuffItem(ItemStack item) {
        for (CuffItem ci : cuffItemsMap.values())
            if (ci.equals(item)) return ci;

        return null;
    }

    public CuffItems getPlugin() {
        return plugin;
    }

    public boolean isHandsBlocked(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        return mainHandItem.equals(handsBlocker) && offHandItem.equals(handsBlocker);
    }

    public boolean hasCuffItemsImmune(Player player) {
        return player.hasPermission("ci.perms.blocker_immune");
    }

    public enum CuffItemType {
        HANDCUFFS ("handcuffs"),
        ROPE ("rope"),
        PSYCHO_JACKET ("psycho_jacket"),
        SHACKLES ("shackles");

        private final String value;
        CuffItemType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static CuffItemType fromString(String str) {
            for (CuffItemType stringValue : CuffItemType.values())
                if (stringValue.toString().equalsIgnoreCase(str)) return stringValue;

            return null;
        }
    }
}
