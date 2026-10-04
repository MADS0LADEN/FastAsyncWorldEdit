package com.fastasyncworldedit.bukkit.util;

import com.fastasyncworldedit.core.util.FoliaUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

/**
 * Runs a task on the region thread that owns a chunk. On Paper and Spigot the task runs inline.
 */
public final class FoliaRegions {

    private FoliaRegions() {
    }

    public static void runOnChunkAndWait(World world, int chunkX, int chunkZ, Runnable task) {
        if (!FoliaUtil.isFoliaServer() || world == null || Bukkit.isOwnedByCurrentRegion(world, chunkX, chunkZ)) {
            task.run();
            return;
        }
        CompletableFuture<Void> done = new CompletableFuture<>();
        Bukkit.getServer().getRegionScheduler().execute(
                WorldEditPlugin.getInstance(),
                world,
                chunkX,
                chunkZ,
                () -> {
                    try {
                        task.run();
                        done.complete(null);
                    } catch (Throwable throwable) {
                        done.completeExceptionally(throwable);
                    }
                }
        );
        done.join();
    }
}
