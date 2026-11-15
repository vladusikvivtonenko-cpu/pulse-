# EzVisuals (Fabric 1.21.4)

Client-side Fabric mod: HUD, visuals, combat helpers and a custom in-game menu.

## Download

GitHub -> **Actions** -> latest **Build** run -> **Artifacts** -> `ezvisuals-jar`.
Put the jar into `.minecraft/mods` together with [Fabric API](https://modrinth.com/mod/fabric-api).

**Requirements:** Minecraft 1.21.4, Fabric Loader 0.16+, Fabric API, Java 21.

## Usage

Menu: **Right Shift** (rebind in Controls -> EzVisuals). `Tab` / `Shift+Tab` switch tabs inside the menu.

## Modules

| Category | Modules |
|----------|---------|
| Combat | PvP Safe, Fast Swap, Command Binds, Totem Helper, FT Helper, Predictions |
| Movement | Full Bridge, Zoom, Auto Sprint |
| Visuals | No Fire, No Shake, Player Glow, Armor Name Tags, Item Highlight, Weather FX |
| HUD | Watermark, Armor HUD, Potion HUD, Cooldowns, Look Slots |
| Optimize | Performance Mode, Auto Quality |

8 colour themes, per-module keybinds (toggle/hold), configs saved to `.minecraft/config/ezvisuals/`.

## Custom images (no rebuild needed)

In `.minecraft/config/ezvisuals/`:

```
mascot.png              -> mascot in the menu sidebar
pictures/1.png..4.png   -> "Custom Pictures" tab
```

## Build locally

```bash
gradle build        # Gradle 8.12+, Java 21
gradle runClient
```

The jar appears in `build/libs/`.

## Disclaimer

Some servers prohibit client mods that give a gameplay advantage (auto bridge, auto totem, fast swap, glow, trajectory prediction). Use at your own risk and follow server rules.
