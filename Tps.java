package com.ezvisuals;

import java.util.Locale;

/** Client-side TPS estimate from the interval between world time update packets (sent about once per second). */
public final class Tps {
    private Tps() {}
    private static long lastNs;
    private static double tps = -1;

    public static void onUpdate() {
        long now = System.nanoTime();
        if (lastNs != 0) {
            double sec = (now - lastNs) / 1e9;
            if (sec > 0.05) {
                double v = Math.max(0.0, Math.min(20.0, 20.0 / sec));
                tps = tps < 0 ? v : tps * 0.6 + v * 0.4;
            }
        }
        lastNs = now;
    }

    public static String str() {
        if (tps < 0) return "--";
        // no packet for a long time -> server is lagging badly
        if (lastNs != 0 && (System.nanoTime() - lastNs) / 1e9 > 5.0) return "0.0";
        return String.format(Locale.ROOT, "%.1f", tps);
    }
}
