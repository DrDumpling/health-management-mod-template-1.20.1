package net.dumploid.healthmanagementmod.util;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerCopyHandler implements ServerPlayerEvents.CopyFrom {
    @Override
    public void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        ((IEntityDataSaver) newPlayer).getPersistentData().putInt("deathCount",
                ((IEntityDataSaver) oldPlayer).getPersistentData().getInt("deathCount")
        );

        ((IEntityDataSaver) newPlayer).getPersistentData().putInt("playerKillCount",
                ((IEntityDataSaver) oldPlayer).getPersistentData().getInt("playerKillCount")
        );
    }
}
