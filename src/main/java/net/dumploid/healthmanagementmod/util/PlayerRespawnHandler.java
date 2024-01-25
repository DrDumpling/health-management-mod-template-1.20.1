package net.dumploid.healthmanagementmod.util;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class PlayerRespawnHandler implements ServerPlayerEvents.AfterRespawn {
    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        int playerKillCount = DeathData.getPlayerKillCount((IEntityDataSaver) oldPlayer);

        int totalRemovedHearts = switch(playerKillCount) {
            case 0 -> 0;
            case 1 -> 2;
            case 2 -> 6;
            default -> 12;
        };

        Objects.requireNonNull(newPlayer.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH))
                .addPersistentModifier(new EntityAttributeModifier("foobar", -totalRemovedHearts, EntityAttributeModifier.Operation.ADDITION));

        newPlayer.setHealth(20 - totalRemovedHearts);
    }
}
