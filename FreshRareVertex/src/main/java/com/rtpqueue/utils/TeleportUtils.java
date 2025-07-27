package com.rtpqueue.utils;

import com.rtpqueue.enums.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public class TeleportUtils {

    private static final Random RANDOM = new Random();
    private static final int WORLD_BORDER = 5000;

    public static Location[] findSafeLocations(WorldType worldType, int spawnDistance) {
        World world = findWorld(worldType);
        if (world == null) return null;

        // Generate random locations without any safety checks
        int x = RANDOM.nextInt(WORLD_BORDER) - (WORLD_BORDER / 2);
        int z = RANDOM.nextInt(WORLD_BORDER) - (WORLD_BORDER / 2);

        Location loc1 = generateLocation(world, x, z, worldType);
        Location loc2 = generateLocation(world, x + spawnDistance * 2, z, worldType);

        if (loc1 != null && loc2 != null) {
            loc1.setYaw(90);  // Face east
            loc2.setYaw(270); // Face west
        }

        return new Location[]{loc1, loc2};
    }

    private static Location generateLocation(World world, int x, int z, WorldType worldType) {
        int y;

        if (worldType == WorldType.NETHER) {
            y = RANDOM.nextInt(100) + 32; // Y 32-132
        } else if (worldType == WorldType.END) {
            y = 60; // Fixed Y for End
        } else {
            y = RANDOM.nextInt(150) + 70; // Y 70-220 for Overworld
        }

        return new Location(world, x + 0.5, y, z + 0.5);
    }

    private static World findWorld(WorldType worldType) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == worldType.getEnvironment()) {
                return world;
            }
        }
        return null;
    }
}