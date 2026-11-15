package com.ezvisuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public final class Hud {
    private Hud() {}
    private static ItemStack TOTEM;

    public static void render(DrawContext c, float td) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.options.hudHidden) return;
        boolean lite = Modules.AUTO_Q.enabled && mc.getCurrentFps() < Modules.AUTO_Q.get("fps").i();
        int sw = mc.getWindow().getScaledWidth(), sh = mc.getWindow().getScaledHeight();

        if (Modules.WEATHER.enabled && !lite) Fx.render(c, mc);
        if (Modules.ITEM_HL.enabled) itemHl(c, mc, sw, sh);
        Pred.render(c, mc, td);
        if (Modules.NAMETAGS.enabled && !lite) tags(c, mc, td);

        int y = 6;
        if (Modules.WATERMARK.enabled) y += watermark(c, mc, 6, y) + 4;
        if (Modules.ARMOR_HUD.enabled) y += armor(c, mc, 6, y) + 4;
        if (Modules.POTION_HUD.enabled) y += potions(c, mc, 6, y) + 4;
        if (Modules.COOLDOWNS.enabled) cooldowns(c, mc, sw, sh, td);
        if (Modules.PVP.enabled && Modules.PVP.get("hud").b()) pvp(c, sw);
        if (Modules.TOTEM.enabled && Modules.TOTEM.get("hud").b()) totem(c, mc, sw, sh);
        if (Modules.LOOK.enabled) look(c, mc, sw, sh);
    }

    // ---------------- watermark ----------------
    private static int watermark(DrawContext c, MinecraftClient mc, int x, int y) {
        Theme t = Theme.cur();
        Module m = Modules.WATERMARK;
        List<String> parts = new ArrayList<>();
        if (m.get("name").b()) parts.add(mc.player.getName().getString());
        if (m.get("fps").b()) parts.add(mc.getCurrentFps() + " FPS");
        if (m.get("tps").b()) parts.add("TPS " + Tps.str());
        if (m.get("ping").b() && mc.getNetworkHandler() != null) {
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (e != null) parts.add(e.getLatency() + " ms");
        }
        if (m.get("xyz").b()) {
            parts.add((int) Math.floor(mc.player.getX()) + " " + (int) Math.floor(mc.player.getY()) + " " + (int) Math.floor(mc.player.getZ()));
        }
        String brand = "ezvisuals", sep = "  |  ";
        int bw = Draw.w(brand), tot = bw;
        for (String p : parts) tot += Draw.w(sep) + Draw.w(p);
        int w = tot + 14, h = 16;
        Draw.rrect(c, x, y, w, h, 4, 0xD00E0E14);
        Draw.hgrad(c, x + 3, y, w - 6, 1, t.accent, t.accent2);
        int tx = x + 7;
        Draw.shadow(c, brand, tx, y + 4, t.accent);
        tx += bw;
        for (String p : parts) {
            Draw.text(c, sep, tx, y + 4, 0xFF555566);
            tx += Draw.w(sep);
            Draw.text(c, p, tx, y + 4, 0xFFE6E6F0);
            tx += Draw.w(p);
        }
        return h;
    }

    // ---------------- armor ----------------
    private static int armor(DrawContext c, MinecraftClient mc, int x, int y) {
        int w = 104, h = 16 + 4 * 18;
        Draw.rrect(c, x, y, w, h, 4, 0xD00E0E14);
        int total = U.armorPct(mc.player);
        Draw.shadow(c, "Armor", x + 7, y + 4, 0xFFFFFFFF);
        Draw.textR(c, total < 0 ? "--" : total + "%", x + w - 7, y + 4, total < 0 ? Draw.SUB : U.pctColor(total));
        for (int i = 0; i < 4; i++) {
            ItemStack st = mc.player.getEquippedStack(U.ARMOR[i]);
            int ry = y + 16 + i * 18;
            if (st.isEmpty()) {
                Draw.text(c, "-", x + 26, ry + 5, Draw.SUB);
                continue;
            }
            c.drawItem(st, x + 5, ry + 1);
            int p = U.itemPct(st);
            int col = p < 0 ? Draw.SUB : U.pctColor(p);
            Draw.text(c, p < 0 ? "n/a" : p + "%", x + 26, ry + 3, col);
            c.fill(x + 26, ry + 13, x + w - 7, ry + 15, 0xFF222230);
            if (p > 0) c.fill(x + 26, ry + 13, x + 26 + (w - 33) * p / 100, ry + 15, col);
        }
        return h;
    }

    // ---------------- potions ----------------
    private static int potions(DrawContext c, MinecraftClient mc, int x, int y) {
        List<StatusEffectInstance> list = new ArrayList<>(mc.player.getStatusEffects());
        if (list.isEmpty()) return -4;
        list.sort(Comparator.comparingInt(StatusEffectInstance::getDuration).reversed());
        if (list.size() > 10) list = list.subList(0, 10);
        int maxW = 70;
        List<String> names = new ArrayList<>(), times = new ArrayList<>();
        for (StatusEffectInstance e : list) {
            String n = e.getEffectType().value().getName().getString() + " " + U.roman(e.getAmplifier() + 1);
            String tm = e.isInfinite() ? "inf" : U.time(e.getDuration());
            names.add(n); times.add(tm);
            maxW = Math.max(maxW, Draw.w(n) + Draw.w(tm) + 16);
        }
        int w = maxW + 14, h = 16 + list.size() * 14;
        Draw.rrect(c, x, y, w, h, 4, 0xD00E0E14);
        Draw.shadow(c, "Effects", x + 7, y + 4, 0xFFFFFFFF);
        for (int i = 0; i < list.size(); i++) {
            StatusEffectInstance e = list.get(i);
            int ry = y + 16 + i * 14;
            int col = 0xFF000000 | e.getEffectType().value().getColor();
            c.fill(x + 5, ry + 1, x + 7, ry + 10, col);
            Draw.text(c, names.get(i), x + 11, ry + 1, 0xFFE6E6F0);
            boolean low = !e.isInfinite() && e.getDuration() < 200;
            Draw.textR(c, times.get(i), x + w - 7, ry + 1, low ? 0xFFFF5555 : Draw.SUB);
        }
        return h;
    }

    // ---------------- cooldowns ----------------
    private static void cooldowns(DrawContext c, MinecraftClient mc, int sw, int sh, float td) {
        ItemCooldownManager cm = mc.player.getItemCooldownManager();
        PlayerInventory inv = mc.player.getInventory();
        Set<Item> seen = new HashSet<>();
        List<ItemStack> st = new ArrayList<>();
        List<Float> pr = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ItemStack s = i < 9 ? inv.getStack(i) : mc.player.getOffHandStack();
            if (s.isEmpty() || seen.contains(s.getItem())) continue;
            if (cm.isCoolingDown(s)) {
                seen.add(s.getItem());
                st.add(s);
                pr.add(cm.getCooldownProgress(s, td));
            }
        }
        if (st.isEmpty()) return;
        Theme t = Theme.cur();
        int w = 92, h = 16 + st.size() * 18;
        int x = sw - w - 6, y = sh / 2 - h / 2;
        Draw.rrect(c, x, y, w, h, 4, 0xD00E0E14);
        Draw.shadow(c, "Cooldowns", x + 7, y + 4, 0xFFFFFFFF);
        for (int i = 0; i < st.size(); i++) {
            int ry = y + 16 + i * 18;
            c.drawItem(st.get(i), x + 5, ry + 1);
            float p = pr.get(i);
            Draw.text(c, Math.round(p * 100) + "%", x + 26, ry + 3, 0xFFE6E6F0);
            c.fill(x + 26, ry + 13, x + w - 7, ry + 15, 0xFF222230);
            c.fill(x + 26, ry + 13, x + 26 + (int) ((w - 33) * (1f - p)), ry + 15, t.accent);
        }
    }

    // ---------------- PvP status ----------------
    private static void pvp(DrawContext c, int sw) {
        boolean combat = Core.combatTicks > 0;
        String a = combat ? "IN COMBAT " + String.format(Locale.ROOT, "%.1f", Core.combatTicks / 20f) + "s" : "PvP SAFE";
        String b = combat ? "Do not log out" : "Safe to disconnect";
        int col = combat ? 0xFFFF5555 : 0xFF5CFF7A;
        int w = Math.max(Draw.w(a), Draw.w(b)) + 22, h = 26, x = (sw - w) / 2, y = 6;
        Draw.rrect(c, x, y, w, h, 4, 0xD00E0E14);
        c.fill(x + 3, y, x + w - 3, y + 1, col);
        Draw.textC(c, a, sw / 2, y + 5, col);
        Draw.textC(c, b, sw / 2, y + 15, Draw.SUB);
    }

    // ---------------- totem ----------------
    private static void totem(DrawContext c, MinecraftClient mc, int sw, int sh) {
        if (TOTEM == null) TOTEM = new ItemStack(Items.TOTEM_OF_UNDYING);
        int cnt = Core.totemCount(mc.player);
        boolean off = mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
        int w = 84, h = 22, x = sw / 2 + 98, y = sh - 24;
        Draw.rrect(c, x, y, w, h, 4, 0xD00E0E14);
        c.drawItem(TOTEM, x + 4, y + 3);
        Draw.text(c, "x" + cnt, x + 24, y + 3, cnt > 0 ? 0xFFFFFFFF : 0xFFFF5555);
        Draw.text(c, off ? "offhand OK" : "NO OFFHAND", x + 24, y + 12, off ? 0xFF5CFF7A : 0xFFFF5555);
    }

    // ---------------- look slots ----------------
    private static void look(DrawContext c, MinecraftClient mc, int sw, int sh) {
        Entity e = mc.targetedEntity;
        if (!(e instanceof PlayerEntity pl)) return;
        ItemStack[] st = {pl.getMainHandStack(), pl.getOffHandStack(),
                pl.getEquippedStack(U.ARMOR[0]), pl.getEquippedStack(U.ARMOR[1]),
                pl.getEquippedStack(U.ARMOR[2]), pl.getEquippedStack(U.ARMOR[3])};
        int w = 6 * 22 + 8, h = 46, x = sw / 2 + 16, y = sh / 2 - 22;
        Draw.rrect(c, x, y, w, h, 4, 0xD00E0E14);
        String hp = String.format(Locale.ROOT, "%.1f", pl.getHealth() + pl.getAbsorptionAmount());
        Draw.shadow(c, pl.getName().getString() + "  " + hp + " HP", x + 6, y + 4, 0xFFFFFFFF);
        for (int i = 0; i < 6; i++) {
            int ix = x + 6 + i * 22, iy = y + 16;
            Draw.rrect(c, ix - 1, iy - 1, 18, 18, 3, 0xFF1B1B25);
            if (st[i].isEmpty()) continue;
            c.drawItem(st[i], ix, iy);
            int p = U.itemPct(st[i]);
            if (p >= 0) Draw.scaled(c, p + "%", ix, iy + 18, 0.7f, U.pctColor(p), false);
        }
    }

    // ---------------- item highlight ----------------
    private static void itemHl(DrawContext c, MinecraftClient mc, int sw, int sh) {
        Theme t = Theme.cur();
        Module m = Modules.ITEM_HL;
        PlayerInventory inv = mc.player.getInventory();
        float pl = (float) (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 250.0));
        int baseX = sw / 2 - 90, y = sh - 19;
        for (int i = 0; i < 9; i++) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty()) continue;
            int col = U.hl(s);
            if (col == 0) continue;
            int x = baseX + i * 20 + 2;
            int a = m.get("pulse").b() ? (int) (90 + 110 * pl) : 190;
            if (m.get("fill").b()) c.fill(x - 1, y - 1, x + 17, y + 17, Draw.alpha(col, 0x2C));
            Draw.outline(c, x - 2, y - 2, 20, 20, Draw.alpha(col, a));
        }
        int sx = baseX + inv.selectedSlot * 20 + 2;
        Draw.outline(c, sx - 3, y - 3, 22, 22, Draw.alpha(t.accent, (int) (120 + 120 * pl)));
    }

    // ---------------- name tags ----------------
    private static void tags(DrawContext c, MinecraftClient mc, float td) {
        Module m = Modules.NAMETAGS;
        double maxD = m.get("dist").d();
        boolean items = m.get("items").b(), showHp = m.get("hp").b();
        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        Theme t = Theme.cur();
        int drawn = 0;
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || p.isSpectator()) continue;
            Vec3d pos = p.getLerpedPos(td);
            double dist = pos.distanceTo(cam);
            if (dist > maxD) continue;
            int[] s = Proj.project(mc, new Vec3d(pos.x, pos.y + p.getHeight() + 0.5, pos.z));
            if (s == null) continue;
            if (++drawn > 24) break;

            String name = p.getName().getString();
            String hp = showHp ? String.format(Locale.ROOT, "%.1f", p.getHealth() + p.getAbsorptionAmount()) : "";
            int ap = U.armorPct(p);
            String arm = ap < 0 ? "" : ap + "%";
            int w1 = Draw.w(name) + (showHp ? Draw.w(hp) + 8 : 0) + (arm.isEmpty() ? 0 : Draw.w(arm) + 8);
            int w = Math.max(w1 + 10, items ? 6 * 14 + 8 : 0);
            int h = 14 + (items ? 16 : 0);
            float sc = (float) Math.max(0.6, Math.min(1.0, 1.25 - dist / 90.0));

            MatrixStack ms = c.getMatrices();
            ms.push();
            ms.translate((float) s[0], (float) s[1], 0f);
            ms.scale(sc, sc, 1f);
            int x = -w / 2, y = -h;
            Draw.rrect(c, x, y, w, h, 3, 0xB0101018);
            c.fill(x + 2, y, x + w - 2, y + 1, t.accent);
            int tx = x + 5;
            Draw.text(c, name, tx, y + 3, 0xFFFFFFFF);
            tx += Draw.w(name) + 4;
            if (showHp) {
                float hv = p.getHealth() + p.getAbsorptionAmount();
                Draw.text(c, hp, tx, y + 3, hv > 14 ? 0xFF5CFF7A : hv > 7 ? 0xFFFFD34D : 0xFFFF5555);
                tx += Draw.w(hp) + 4;
            }
            if (!arm.isEmpty()) Draw.text(c, arm, tx, y + 3, U.pctColor(ap));
            if (items) {
                ItemStack[] st = {p.getMainHandStack(), p.getOffHandStack(),
                        p.getEquippedStack(U.ARMOR[0]), p.getEquippedStack(U.ARMOR[1]),
                        p.getEquippedStack(U.ARMOR[2]), p.getEquippedStack(U.ARMOR[3])};
                MatrixStack m2 = c.getMatrices();
                for (int i = 0; i < 6; i++) {
                    if (st[i].isEmpty()) continue;
                    m2.push();
                    m2.translate((float) (x + 4 + i * 14), (float) (y + 15), 0f);
                    m2.scale(0.75f, 0.75f, 1f);
                    c.drawItem(st[i], 0, 0);
                    m2.pop();
                }
            }
            ms.pop();
        }
    }
}
