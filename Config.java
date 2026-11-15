package com.ezvisuals;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private Config() {}
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Path dir() {
        Path d = FabricLoader.getInstance().getConfigDir().resolve("ezvisuals");
        try { Files.createDirectories(d); } catch (IOException ignored) {}
        return d;
    }

    private static Path file(String name) { return dir().resolve(name + ".json"); }

    public static boolean exists(String name) { return Files.exists(file(name)); }

    public static boolean save(String name) {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("theme", Theme.index);
            JsonObject mods = new JsonObject();
            for (Module m : Modules.ALL) {
                JsonObject o = new JsonObject();
                o.addProperty("on", m.enabled);
                o.addProperty("bind", m.bind);
                o.addProperty("hold", m.hold);
                JsonObject s = new JsonObject();
                for (Setting st : m.settings) s.addProperty(st.id, st.value);
                o.add("s", s);
                mods.add(m.id, o);
            }
            root.add("modules", mods);
            root.add("commands", actions(Binds.COMMANDS));
            root.add("swaps", actions(Binds.SWAPS));
            Files.writeString(file(name), GSON.toJson(root), StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static JsonArray actions(Binds.Action[] arr) {
        JsonArray a = new JsonArray();
        for (Binds.Action x : arr) {
            JsonObject o = new JsonObject();
            o.addProperty("text", x.text);
            o.addProperty("bind", x.bind);
            a.add(o);
        }
        return a;
    }

    private static void readActions(JsonObject root, String key, Binds.Action[] arr) {
        if (!root.has(key)) return;
        JsonArray a = root.getAsJsonArray(key);
        for (int i = 0; i < arr.length && i < a.size(); i++) {
            JsonObject o = a.get(i).getAsJsonObject();
            if (o.has("text")) arr[i].text = o.get("text").getAsString();
            if (o.has("bind")) arr[i].bind = o.get("bind").getAsInt();
        }
    }

    public static boolean load(String name) {
        try {
            Path f = file(name);
            if (!Files.exists(f)) return false;
            JsonObject root = JsonParser.parseString(Files.readString(f, StandardCharsets.UTF_8)).getAsJsonObject();
            if (root.has("theme")) Theme.index = Math.max(0, Math.min(Theme.ALL.length - 1, root.get("theme").getAsInt()));
            if (root.has("modules")) {
                JsonObject mods = root.getAsJsonObject("modules");
                for (Module m : Modules.ALL) {
                    if (!mods.has(m.id)) continue;
                    JsonObject o = mods.getAsJsonObject(m.id);
                    if (o.has("on")) m.enabled = o.get("on").getAsBoolean();
                    if (o.has("bind")) m.bind = o.get("bind").getAsInt();
                    if (o.has("hold")) m.hold = o.get("hold").getAsBoolean();
                    if (o.has("s")) {
                        JsonObject s = o.getAsJsonObject("s");
                        for (Setting st : m.settings) if (s.has(st.id)) st.set(s.get(st.id).getAsDouble());
                    }
                    m.anim = m.enabled ? 1f : 0f;
                    for (Setting st : m.settings) st.anim = st.b() ? 1f : 0f;
                }
            }
            readActions(root, "commands", Binds.COMMANDS);
            readActions(root, "swaps", Binds.SWAPS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void reset() {
        Theme.index = 0;
        for (Module m : Modules.ALL) { m.reset(); m.anim = m.enabled ? 1f : 0f; }
        for (Binds.Action a : Binds.COMMANDS) a.reset();
        for (Binds.Action a : Binds.SWAPS) a.reset();
    }
}
