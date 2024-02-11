package org.s1queence.plugin.items;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.s1queence.plugin.classes.CuffManager;
import org.s1queence.plugin.libs.YamlDocument;

import java.util.Map;
import java.util.UUID;

import static org.s1queence.api.S1TextUtils.createItemFromMap;

public abstract class CuffItem extends ItemStack {
    private final ItemStack item;
    private final YamlDocument config;
    private final String name;
    private final int timeToCuff;
    private final CuffManager cm;
    private final CuffManager.CuffItemType cit;

    public CuffItem(YamlDocument config, String name, CuffManager cm, CuffManager.CuffItemType cit) {
        this.config = config;
        this.name = name;
        this.timeToCuff = config.getInt(name + ".time_to_cuff");
        this.cm = cm;
        this.cit = cit;
        this.item = create();
    }

    protected ItemStack create() {
        Map<String, Object> mappedItem = config.getSection(name).getStringRouteMappedValues(true);
        ItemStack is = createItemFromMap(mappedItem);
        if (is == null) return null;

        ItemMeta im = is.getItemMeta();
        if (im == null) return is;
        AttributeModifier armorLevelMod = new AttributeModifier(UUID.randomUUID(), "generic.armorLevel", 0.0d, AttributeModifier.Operation.ADD_NUMBER, cm.getArmorType(item));
        im.addAttributeModifier(Attribute.GENERIC_ARMOR, armorLevelMod);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        is.setItemMeta(im);

        return is;
    }

    public ItemStack getItem() {
        return item;
    }
    public int getTimeToCuff() {
        return timeToCuff;
    }
    public CuffManager.CuffItemType getCuffItemType() {
        return cit;
    }

    public boolean equals(ItemStack toCheck) {
        if (toCheck == null) return false;
        if (item.equals(toCheck)) return true;
        ItemMeta cuffItemIM = item.getItemMeta();
        ItemMeta toCheckIM = toCheck.getItemMeta();
        if (toCheckIM == null) return false;
        if (cuffItemIM == null) return false;
        boolean isTypeMatch = toCheck.getType().equals(item.getType());
        boolean isNamesMatch = cuffItemIM.getDisplayName().equals(toCheckIM.getDisplayName());
        boolean isLoresMatch = cuffItemIM.getLore() == null || cuffItemIM.getLore().equals(toCheckIM.getLore());
        boolean hasCMD = cuffItemIM.hasCustomModelData();
        boolean isCMDsMatch = toCheckIM.hasCustomModelData() && cuffItemIM.getCustomModelData() == toCheckIM.getCustomModelData();

        return hasCMD ? isLoresMatch && isNamesMatch && isTypeMatch && isCMDsMatch : isLoresMatch && isNamesMatch && isTypeMatch;
    }
}
