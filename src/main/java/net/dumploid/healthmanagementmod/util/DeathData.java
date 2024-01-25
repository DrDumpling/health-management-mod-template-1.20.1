package net.dumploid.healthmanagementmod.util;

import net.minecraft.nbt.NbtCompound;

public class DeathData {
    public static void incrementDeaths(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        int deathCount = nbt.getInt("deathCount");

        deathCount += 1;

        nbt.putInt("deathCount", deathCount);

    }

    public static int getDeathCount(IEntityDataSaver player) {
        return player.getPersistentData().getInt("deathCount");
    }

    public static void incrementPlayerKillCount(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        int killCount = nbt.getInt("playerKillCount");

        killCount += 1;

        nbt.putInt("playerKillCount", killCount);
    }

    public static int getPlayerKillCount(IEntityDataSaver player) {
        return player.getPersistentData().getInt("playerKillCount");
    }
}
