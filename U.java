package com.ezvisuals;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

/** Small helpers shared by the HUD. */
public final class U {
    private U() {}

    public static final EquipmentSlot[] ARMOR = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    /** Durability left in percent, -1 if the item has no durability. */
    public static int itemPct(ItemStack s) {
        if (s == null || s.isEmpty() || !s.isDamageable()) return -1;
        int max = s.getMaxDamage();
        if (max <= 0) return -1;
        return Math.max(0, Math.min(100, (max - s.getDamage()) * 100 / max));
    }

    /** Average durability of worn armor in percent, -1 if no damageable armor is worn. */
    public static int armorPct(LivingEntity e) {
        int sum = 0, n = 0;
        for (EquipmentSlot slot : ARMOR) {
            int p = itemPct(e.getEquippedStack(slot));
            if (p >= 0) { sum += p; n++; }
        }
        return n == 0 ? -1 : sum / n;
    }

    public static int pctColor(int p) {
        if (p >= 60) return 0xFF5CFF7A;
        if (p >= 30) return 0xFFFFD34D;
        return 0xFFFF5555;
    }

    /** Hotbar highlight colour for an item, 0 = no highlight. */
    public static int hl(ItemStack s) {
        if (s.isEmpty()) return 0;
        if (s.isOf(Items.TOTEM_OF_UNDYING)) return 0xFFFFD34D;
        if (s.isIn(ItemTags.SWORDS)) return 0xFFFF5555;
        if (s.isIn(ItemTags.PICKAXES)) return 0xFF4DD2FF;
        if (s.isIn(ItemTags.AXES)) return 0xFFFF9E40;
        if (s.isOf(Items.BOW) || s.isOf(Items.CROSSBOW)) return 0xFFB36BFF;
        return 0;
    }

    public static String roman(int n) {
        String[] r = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return n >= 1 && n < r.length ? r[n] : String.valueOf(n);
    }

    /** Ticks -> m:ss */
    public static String time(int ticks) {
        int s = Math.max(0, ticks / 20);
        return (s / 60) + ":" + (s % 60 < 10 ? "0" : "") + (s % 60);
    }
}
