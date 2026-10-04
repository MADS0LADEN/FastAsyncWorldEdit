package com.fastasyncworldedit.core.util;

import java.lang.reflect.Method;

/**
 * Detects Folia and Folia forks such as CanvasMC without linking against their API.
 * {@code worldedit-core} cannot depend on Bukkit, so every check is reflective.
 */
public final class FoliaUtil {

    private static final boolean FOLIA_DETECTED = detectFolia();

    private static final Method IS_GLOBAL_TICK_THREAD = resolveIsGlobalTickThread();

    private FoliaUtil() {
    }

    /**
     * @return true when the running server is Folia or a regionised fork such as CanvasMC
     */
    public static boolean isFoliaServer() {
        return FOLIA_DETECTED;
    }

    /**
     * @return true when the current thread is the global region tick thread
     */
    public static boolean isGlobalTickThread() {
        if (IS_GLOBAL_TICK_THREAD == null) {
            return false;
        }
        try {
            return (boolean) IS_GLOBAL_TICK_THREAD.invoke(null);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private static boolean detectFolia() {
        // RegionizedServer is the Folia marker. Canvas keeps it, but a fork may move one class,
        // so also accept RegionizedWorldData and Canvas's own config type. Do not treat
        // Bukkit#isGlobalTickThread as proof: Paper exposes that method and is not regionised.
        return hasClass("io.papermc.paper.threadedregions.RegionizedServer")
                || hasClass("io.papermc.paper.threadedregions.RegionizedWorldData")
                || hasClass("io.canvasmc.canvas.Config");
    }

    private static boolean hasClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static Method resolveIsGlobalTickThread() {
        try {
            return Class.forName("org.bukkit.Bukkit").getMethod("isGlobalTickThread");
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
