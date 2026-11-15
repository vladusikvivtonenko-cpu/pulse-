package com.ezvisuals;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class Modules {
    private Modules() {}
    public static final List<Module> ALL = new ArrayList<>();
    private static Module add(Module m) { ALL.add(m); return m; }

    // ---------------- COMBAT ----------------
    public static final Module PVP = add(new Module("pvpsafe", "PvP Safe", "Combat",
            "Combat timer: shows if it is safe to log out, blocks command binds in combat", true,
            Setting.num("time", "Combat time (s)", 15, 5, 60, 1),
            Setting.bool("block", "Block commands in combat", true),
            Setting.bool("hud", "Show status", true)));

    public static final Module FASTSWAP = add(new Module("fastswap", "Fast Swap", "Combat",
            "Binds on special items (trap, disorient...). Set names in Binds tab", true,
            Setting.bool("use", "Use item after swap", true),
            Setting.bool("back", "Swap back", true)));

    public static final Module CMDBIND = add(new Module("cmdbind", "Command Binds", "Combat",
            "Run /home /hub /darena etc. on a key. Set in Binds tab", true));

    public static final Module TOTEM = add(new Module("totem", "Totem Helper", "Combat",
            "Totem counter, offhand warning and optional auto swap", true,
            Setting.bool("hud", "Show counter", true),
            Setting.bool("auto", "Auto swap to offhand", false),
            Setting.num("hp", "Swap at HP", 6, 2, 19, 1)));

    public static final Module FT = add(new Module("fthelper", "FT Helper", "Combat",
            "Circle around you: plast (kelp), disorient (ender eye), freeze (snowball), crossbow. Potions: landing circle", true,
            Setting.bool("text", "Show radius text", true),
            Setting.num("potion", "Potion radius (landing)", 4, 1, 20, 0.5),
            Setting.num("plast", "Plast radius", 3, 1, 15, 0.5),
            Setting.num("disor", "Disorientation radius", 10, 1, 25, 0.5),
            Setting.num("freeze", "Freeze snowball radius", 7, 1, 15, 0.5),
            Setting.num("cross", "Crossbow radius", 5, 1, 15, 0.5)));

    public static final Module PREDICT = add(new Module("predict", "Predictions", "Combat",
            "Trajectory of bow, crossbow, trident, pearl, snowball, potions", true,
            Setting.bool("bow", "Bow", true),
            Setting.bool("cross", "Crossbow", true),
            Setting.bool("trident", "Trident", true),
            Setting.bool("thr", "Pearl / snowball / egg / potion", true),
            Setting.bool("marker", "Landing marker + distance", true),
            Setting.mode("color", "Color", 0, "Theme", "White", "Red")));

    // ---------------- MOVEMENT ----------------
    public static final Module BRIDGE = add(new Module("bridge", "Full Bridge", "Movement",
            "Auto places blocks at the edge while you walk (hold blocks)", false,
            Setting.num("delay", "Place delay (ticks)", 2, 1, 10, 1),
            Setting.bool("sneak", "Only while sneaking", false),
            Setting.bool("swing", "Swing hand", true)));

    public static final Module ZOOM = add(new Module("zoom", "Zoom", "Movement",
            "Zoom view. Click the bind button: left = set key, right = hold/toggle", false,
            Setting.num("fov", "Zoom FOV", 30, 30, 70, 1)).bind(GLFW.GLFW_KEY_C, true));

    public static final Module SPRINT = add(new Module("sprint", "Auto Sprint", "Movement",
            "Keeps you sprinting while moving forward", false));

    // ---------------- VISUALS ----------------
    public static final Module NOFIRE = add(new Module("nofire", "No Fire", "Visuals",
            "Hides the fire overlay on your screen", false));

    public static final Module NOSHAKE = add(new Module("noshake", "No Shake", "Visuals",
            "Disables the hurt camera shake", false));

    public static final Module ESP = add(new Module("esp", "Player Glow", "Visuals",
            "Outline glow on other players", false));

    public static final Module NAMETAGS = add(new Module("nametags", "Armor Name Tags", "Visuals",
            "Name tags with HP, armor percent and equipment", true,
            Setting.num("dist", "Max distance", 64, 8, 128, 4),
            Setting.bool("items", "Show items", true),
            Setting.bool("hp", "Show HP", true)));

    public static final Module ITEM_HL = add(new Module("itemhl", "Item Highlight", "Visuals",
            "Colored highlight of swords, pickaxes, axes, bows, totems in hotbar", true,
            Setting.bool("fill", "Color fill", true),
            Setting.bool("pulse", "Pulse", true)));

    public static final Module WEATHER = add(new Module("weather", "Weather FX", "Visuals",
            "Rain / snow / space overlay effect", false,
            Setting.mode("type", "Effect", 0, "Rain", "Snow", "Space"),
            Setting.num("dens", "Density", 80, 10, 300, 10),
            Setting.num("speed", "Speed", 1.0, 0.3, 3.0, 0.1),
            Setting.bool("tint", "Screen tint", true)));

    // ---------------- HUD ----------------
    public static final Module WATERMARK = add(new Module("watermark", "Watermark", "HUD",
            "ezvisuals bar with name, FPS, TPS, ping, coordinates", true,
            Setting.bool("name", "Nickname", true),
            Setting.bool("fps", "FPS", true),
            Setting.bool("tps", "TPS", true),
            Setting.bool("ping", "Ping", true),
            Setting.bool("xyz", "Coordinates", true)));

    public static final Module ARMOR_HUD = add(new Module("armorhud", "Armor HUD", "HUD",
            "Armor durability percent (total and per piece)", true));

    public static final Module POTION_HUD = add(new Module("potionhud", "Potion HUD", "HUD",
            "Active effects with remaining time", true));

    public static final Module COOLDOWNS = add(new Module("cooldowns", "Cooldowns", "HUD",
            "Item cooldowns (pearl, chorus, totem...)", true));

    public static final Module LOOK = add(new Module("lookslots", "Look Slots", "HUD",
            "Equipment of the player you are looking at", false));

    // ---------------- OPTIMIZE ----------------
    public static final Module PERF = add(new Module("perf", "Performance Mode", "Optimize",
            "Lowers heavy video options for weak PCs, restores them when turned off", false,
            Setting.mode("particles", "Particles", 2, "All", "Decreased", "Minimal"),
            Setting.bool("clouds", "Disable clouds", true),
            Setting.bool("shadows", "Disable entity shadows", true),
            Setting.bool("fast", "Fast graphics", true),
            Setting.num("dist", "Entity distance", 0.5, 0.5, 1.0, 0.1),
            Setting.bool("blend", "No biome blend", true)));

    public static final Module AUTO_Q = add(new Module("autoq", "Auto Quality", "Optimize",
            "Turns off Weather FX and name tags when FPS drops", true,
            Setting.num("fps", "Min FPS", 30, 15, 60, 5)));
}
